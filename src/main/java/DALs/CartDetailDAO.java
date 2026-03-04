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

    /** Thêm hoặc cộng dồn: nếu đã có (customer_id, variant_id) thì cập nhật quantity = quantity + addQuantity, không thì insert. */
    public void addOrUpdate(int customerId, int variantId, int addQuantity) {
        String findSql = "SELECT cart_detail_id, quantity FROM Cart_Detail WHERE customer_id = ? AND variant_id = ?";
        try (PreparedStatement psFind = connection.prepareStatement(findSql)) {
            psFind.setInt(1, customerId);
            psFind.setInt(2, variantId);
            try (ResultSet rs = psFind.executeQuery()) {
                if (rs.next()) {
                    int cartDetailId = rs.getInt("cart_detail_id");
                    int currentQty = rs.getInt("quantity");
                    int newQty = currentQty + addQuantity;
                    if (newQty <= 0) {
                        delete(customerId, variantId);
                    } else {
                        String updateSql = "UPDATE Cart_Detail SET quantity = ? WHERE cart_detail_id = ?";
                        try (PreparedStatement psUp = connection.prepareStatement(updateSql)) {
                            psUp.setInt(1, newQty);
                            psUp.setInt(2, cartDetailId);
                            psUp.executeUpdate();
                        }
                    }
                } else {
                    if (addQuantity > 0) {
                        String insertSql = "INSERT INTO Cart_Detail (customer_id, variant_id, quantity) VALUES (?, ?, ?)";
                        try (PreparedStatement psIns = connection.prepareStatement(insertSql)) {
                            psIns.setInt(1, customerId);
                            psIns.setInt(2, variantId);
                            psIns.setInt(3, addQuantity);
                            psIns.executeUpdate();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Đặt số lượng (update/delete): quantity <= 0 thì xóa dòng. */
    public void setQuantity(int customerId, int variantId, int quantity) {
        if (quantity <= 0) {
            delete(customerId, variantId);
            return;
        }
        String sql = "UPDATE Cart_Detail SET quantity = ? WHERE customer_id = ? AND variant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, customerId);
            ps.setInt(3, variantId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                String insertSql = "INSERT INTO Cart_Detail (customer_id, variant_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement psIns = connection.prepareStatement(insertSql)) {
                    psIns.setInt(1, customerId);
                    psIns.setInt(2, variantId);
                    psIns.setInt(3, quantity);
                    psIns.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
