package DALs;

import Model.Voucher;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO extends DBContext {

    public List<Voucher> getAll() {
        String sql = "SELECT * FROM Voucher ORDER BY voucher_id DESC";
        List<Voucher> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapVoucher(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Voucher getById(int voucherId) {
        String sql = "SELECT * FROM Voucher WHERE voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVoucher(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Voucher getByCode(String code) {
        String sql = "SELECT * FROM Voucher WHERE code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVoucher(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Voucher voucher) {
        String sql = "INSERT INTO Voucher (code, discount_type, discount_value, quantity, expired_at, status, "
                + "voucher_type, fixed_start_at, fixed_end_at, relative_days, claimed_quantity, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setVoucherParams(ps, voucher, false);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Voucher voucher) {
        String sql = "UPDATE Voucher SET code = ?, discount_type = ?, discount_value = ?, quantity = ?, expired_at = ?, "
                + "status = ?, voucher_type = ?, fixed_start_at = ?, fixed_end_at = ?, relative_days = ?, "
                + "claimed_quantity = ? WHERE voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setVoucherParams(ps, voucher, true);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(int voucherId) {
        String sql = "UPDATE Voucher SET status = 0 WHERE voucher_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setVoucherParams(PreparedStatement ps, Voucher voucher, boolean includeIdAtEnd) throws Exception {
        ps.setString(1, voucher.getCode());
        ps.setString(2, voucher.getDiscountType());
        ps.setBigDecimal(3, voucher.getDiscountValue());
        ps.setInt(4, voucher.getQuantity());
        ps.setObject(5, voucher.getExpiredAt());
        ps.setBoolean(6, Boolean.TRUE.equals(voucher.getStatus()));
        ps.setString(7, voucher.getVoucherType());
        ps.setObject(8, voucher.getFixedStartAt());
        ps.setObject(9, voucher.getFixedEndAt());
        if (voucher.getRelativeDays() == null) {
            ps.setNull(10, java.sql.Types.INTEGER);
        } else {
            ps.setInt(10, voucher.getRelativeDays());
        }
        ps.setInt(11, voucher.getClaimedQuantity());
        if (includeIdAtEnd) {
            ps.setInt(12, voucher.getVoucherId());
        } else {
            LocalDateTime created = voucher.getCreatedAt() == null ? LocalDateTime.now() : voucher.getCreatedAt();
            ps.setObject(12, created);
        }
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
