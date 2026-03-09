package DALs;

import Model.CustomerVoucher;
import Model.Voucher;
import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerVoucherDAO extends DBContext {

    public List<CustomerVoucher> getByCustomerId(int customerId) {
        String sql = "SELECT cv.customer_voucher_id, cv.customer_id, cv.voucher_id, cv.claimed_at, cv.effective_from, "
                + "cv.effective_to, cv.status AS customer_voucher_status, cv.used_at, "
                + "v.code, v.discount_type, v.discount_value, v.voucher_type "
                + "FROM Customer_Voucher cv "
                + "JOIN Voucher v ON v.voucher_id = cv.voucher_id "
                + "WHERE cv.customer_id = ? "
                + "ORDER BY cv.claimed_at DESC";

        List<CustomerVoucher> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCustomerVoucher(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CustomerVoucher> getActiveByCustomerId(int customerId) {
        String sql = "SELECT cv.customer_voucher_id, cv.customer_id, cv.voucher_id, cv.claimed_at, cv.effective_from, "
                + "cv.effective_to, cv.status AS customer_voucher_status, cv.used_at, "
                + "v.code, v.discount_type, v.discount_value, v.voucher_type "
                + "FROM Customer_Voucher cv "
                + "JOIN Voucher v ON v.voucher_id = cv.voucher_id "
                + "WHERE cv.customer_id = ? "
                + "AND cv.status = 'ACTIVE' "
                + "ORDER BY cv.claimed_at DESC";

        List<CustomerVoucher> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCustomerVoucher(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean claimVoucher(int customerId, String code) {
        return "ok".equals(claimVoucherWithReason(customerId, code));
    }

    public String claimVoucherWithReason(int customerId, String code) {
        Connection conn = null;
        try {
            conn = this.connection;
            if (conn == null) {
                return "dbError";
            }
            conn.setAutoCommit(false);

            String findVoucherSql = "SELECT * FROM Voucher WHERE LTRIM(RTRIM(code)) = LTRIM(RTRIM(?)) AND status = 1";
            Voucher voucher;
            try (PreparedStatement ps = conn.prepareStatement(findVoucherSql)) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return "codeNotFound";
                    }
                    voucher = mapVoucher(rs);
                }
            }

            if (voucher.getClaimedQuantity() >= voucher.getQuantity()) {
                conn.rollback();
                return "outOfStock";
            }

            String duplicateSql = "SELECT 1 FROM Customer_Voucher WHERE customer_id = ? AND voucher_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(duplicateSql)) {
                ps.setInt(1, customerId);
                ps.setInt(2, voucher.getVoucherId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        conn.rollback();
                        return "alreadyClaimed";
                    }
                }
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime effectiveFrom;
            LocalDateTime effectiveTo;

            if ("FIXED_END_DATE".equalsIgnoreCase(voucher.getVoucherType())) {
                effectiveFrom = voucher.getFixedStartAt() != null ? voucher.getFixedStartAt() : now;
                effectiveTo = voucher.getFixedEndAt() != null ? voucher.getFixedEndAt() : voucher.getExpiredAt();
            } else {
                Integer days = voucher.getRelativeDays() == null ? 0 : voucher.getRelativeDays();
                effectiveFrom = now;
                effectiveTo = now.plusDays(days);
            }

            if (effectiveTo == null || effectiveTo.isBefore(now)) {
                conn.rollback();
                return "voucherExpired";
            }

            String insertSql = "INSERT INTO Customer_Voucher (customer_id, voucher_id, claimed_at, effective_from, "
                    + "effective_to, status, used_at) VALUES (?, ?, ?, ?, ?, 'ACTIVE', NULL)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, customerId);
                ps.setInt(2, voucher.getVoucherId());
                ps.setObject(3, now);
                ps.setObject(4, effectiveFrom);
                ps.setObject(5, effectiveTo);
                ps.executeUpdate();
            }

            String updateClaimedSql = "UPDATE Voucher SET claimed_quantity = claimed_quantity + 1 WHERE voucher_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateClaimedSql)) {
                ps.setInt(1, voucher.getVoucherId());
                ps.executeUpdate();
            }

            conn.commit();
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ignored) {
            }
            return "dbError";
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public CustomerVoucher getActiveVoucherForCheckout(int customerId, String code) {
        String sql = "SELECT TOP 1 cv.customer_voucher_id, cv.customer_id, cv.voucher_id, cv.claimed_at, cv.effective_from, "
                + "cv.effective_to, cv.status AS customer_voucher_status, cv.used_at, "
                + "v.code, v.discount_type, v.discount_value, v.voucher_type "
                + "FROM Customer_Voucher cv "
                + "JOIN Voucher v ON v.voucher_id = cv.voucher_id "
                + "WHERE cv.customer_id = ? "
                + "AND v.code = ? "
                + "AND cv.status = 'ACTIVE' "
                + "AND GETDATE() BETWEEN cv.effective_from AND cv.effective_to "
                + "AND v.status = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomerVoucher(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean markVoucherUsed(int customerVoucherId) {
        String sql = "UPDATE Customer_Voucher SET status = 'USED', used_at = GETDATE() "
                + "WHERE customer_voucher_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerVoucherId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int expireOutdatedVouchers() {
        String sql = "UPDATE Customer_Voucher SET status = 'EXPIRED' "
                + "WHERE status = 'ACTIVE' AND effective_to < GETDATE()";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private CustomerVoucher mapCustomerVoucher(ResultSet rs) throws Exception {
        CustomerVoucher cv = new CustomerVoucher();
        cv.setCustomerVoucherId(rs.getInt("customer_voucher_id"));
        cv.setCustomerId(rs.getInt("customer_id"));
        cv.setVoucherId(rs.getInt("voucher_id"));
        cv.setClaimedAt(rs.getObject("claimed_at", LocalDateTime.class));
        cv.setEffectiveFrom(rs.getObject("effective_from", LocalDateTime.class));
        cv.setEffectiveTo(rs.getObject("effective_to", LocalDateTime.class));
        cv.setStatus(rs.getString("customer_voucher_status"));
        cv.setUsedAt(rs.getObject("used_at", LocalDateTime.class));

        Voucher voucher = new Voucher();
        voucher.setVoucherId(cv.getVoucherId());
        voucher.setCode(rs.getString("code"));
        voucher.setDiscountType(rs.getString("discount_type"));
        voucher.setDiscountValue(rs.getBigDecimal("discount_value"));
        voucher.setVoucherType(rs.getString("voucher_type"));
        cv.setVoucher(voucher);
        return cv;
    }

    private Voucher mapVoucher(ResultSet rs) throws Exception {
        Voucher voucher = new Voucher();
        voucher.setVoucherId(rs.getInt("voucher_id"));
        voucher.setCode(rs.getString("code"));
        voucher.setDiscountType(rs.getString("discount_type"));
        voucher.setDiscountValue(rs.getBigDecimal("discount_value"));
        voucher.setQuantity(rs.getInt("quantity"));
        voucher.setExpiredAt(rs.getObject("expired_at", LocalDateTime.class));
        voucher.setStatus(rs.getBoolean("status"));
        voucher.setVoucherType(rs.getString("voucher_type"));
        voucher.setFixedStartAt(rs.getObject("fixed_start_at", LocalDateTime.class));
        voucher.setFixedEndAt(rs.getObject("fixed_end_at", LocalDateTime.class));
        voucher.setRelativeDays(rs.getObject("relative_days", Integer.class));
        voucher.setClaimedQuantity(rs.getInt("claimed_quantity"));
        voucher.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return voucher;
    }
}
