package DALs;

import Utils.DBContext;
import Model.CartDetail;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CartDetailDAO extends DBContext {

    public List<CartDetail> getByCustomerId(int customerId) {
        List<CartDetail> list = new ArrayList<>();
        String sql = "SELECT cart_detail_id, customer_id, variant_id, quantity FROM Cart_Detail WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CartDetail(
                            rs.getInt("cart_detail_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("variant_id"),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- HÀM HỖ TRỢ LẤY STOCK ---
    private int getVariantStock(int variantId) {
        String sql = "SELECT stock FROM Product_Variant WHERE variant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** * Cập nhật số lượng có check stock.
     * @return số lượng thực tế đã được set vào giỏ hàng (nếu out of stock sẽ trả về stock tối đa) 
     */
    public int addOrUpdate(int customerId, int variantId, int addQuantity) {
        int stock = getVariantStock(variantId);
        if (stock <= 0) return 0; // Hết hàng thì không cho thêm

        String findSql = "SELECT cart_detail_id, quantity FROM Cart_Detail WHERE customer_id = ? AND variant_id = ?";
        try (PreparedStatement psFind = connection.prepareStatement(findSql)) {
            psFind.setInt(1, customerId);
            psFind.setInt(2, variantId);
            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    int cartDetailId = rs.getInt("cart_detail_id");
                    int currentQty = rs.getInt("quantity");
                    int newQty = currentQty + addQuantity;
                    
                    // --- BẮT ĐẦU CHECK STOCK ---
                    if (newQty > stock) {
                        newQty = stock; // Ép về max stock nếu vượt
                    }
                    // ---------------------------

                    if (newQty <= 0) {
                        delete(customerId, variantId);
                        return 0;
                    } else {
                        String updateSql = "UPDATE Cart_Detail SET quantity = ? WHERE cart_detail_id = ?";
                        try (PreparedStatement psUp = connection.prepareStatement(updateSql)) {
                            psUp.setInt(1, newQty);
                            psUp.setInt(2, cartDetailId);
                            psUp.executeUpdate();
                        }
                        return newQty;
                    }
                } else {
                    if (addQuantity > 0) {
                        int finalAddQty = Math.min(addQuantity, stock); // Check stock cho lần thêm mới
                        String insertSql = "INSERT INTO Cart_Detail (customer_id, variant_id, quantity) VALUES (?, ?, ?)";
                        try (PreparedStatement psIns = connection.prepareStatement(insertSql)) {
                            psIns.setInt(1, customerId);
                            psIns.setInt(2, variantId);
                            psIns.setInt(3, finalAddQty);
                            psIns.executeUpdate();
                        }
                        return finalAddQty;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /** Đặt số lượng (có check stock) */
    public int setQuantity(int customerId, int variantId, int quantity) {
        if (quantity <= 0) {
            delete(customerId, variantId);
            return 0;
        }

        int stock = getVariantStock(variantId);
        int finalQty = Math.min(quantity, stock); // Không cho phép set vượt stock

        if (finalQty == 0) {
            delete(customerId, variantId);
            return 0;
        }

        String sql = "UPDATE Cart_Detail SET quantity = ? WHERE customer_id = ? AND variant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, finalQty);
            ps.setInt(2, customerId);
            ps.setInt(3, variantId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                String insertSql = "INSERT INTO Cart_Detail (customer_id, variant_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement psIns = connection.prepareStatement(insertSql)) {
                    psIns.setInt(1, customerId);
                    psIns.setInt(2, variantId);
                    psIns.setInt(3, finalQty);
                    psIns.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalQty;
    }

    /**
     * TÍNH NĂNG MỚI: Đổi phân loại ngay trong giỏ hàng
     */
    public boolean changeVariant(int customerId, int oldVariantId, int newVariantId) {
        if (oldVariantId == newVariantId) return true;

        String findSql = "SELECT quantity FROM Cart_Detail WHERE customer_id = ? AND variant_id = ?";
        try (PreparedStatement psFind = connection.prepareStatement(findSql)) {
            psFind.setInt(1, customerId);
            psFind.setInt(2, oldVariantId);
            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    // Xóa variant cũ
                    delete(customerId, oldVariantId);
                    // Thêm số lượng đó vào variant mới (hàm addOrUpdate sẽ tự gộp nếu variant mới đã có sẵn, và tự check stock luôn)
                    addOrUpdate(customerId, newVariantId, currentQty);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void delete(int customerId, int variantId) {
        String sql = "DELETE FROM Cart_Detail WHERE customer_id = ? AND variant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, variantId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deleteAll(int customerId) {
        String sql = "DELETE FROM Cart_Detail WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}