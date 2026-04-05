package DALs;

import Model.Supplier;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO extends DBContext {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT supplier_id, name, phone, address, status "
                + "FROM Supplier "
                + "ORDER BY supplier_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT supplier_id, name, phone, address, status "
                + "FROM Supplier "
                + "WHERE supplier_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertSupplier(Supplier supplier) {
        String sql = "INSERT INTO Supplier (name, phone, address, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillSupplierStatement(ps, supplier, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Supplier SET name = ?, phone = ?, address = ?, status = ? WHERE supplier_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillSupplierStatement(ps, supplier, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM Supplier WHERE name = ?";
        return existsByName(sql, name, null);
    }

    public boolean existsByNameExceptId(String name, int excludeId) {
        String sql = "SELECT 1 FROM Supplier WHERE name = ? AND supplier_id <> ?";
        return existsByName(sql, name, excludeId);
    }

    public List<Supplier> getAllActiveSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT supplier_id, name, phone, address, status "
                + "FROM Supplier "
                + "WHERE status = 1 "
                + "ORDER BY name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapSupplier(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    private Supplier mapSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setName(rs.getString("name"));
        supplier.setPhone(rs.getString("phone"));
        supplier.setAddress(rs.getString("address"));
        supplier.setStatus(rs.getBoolean("status"));
        return supplier;
    }

    private void fillSupplierStatement(PreparedStatement ps, Supplier supplier, boolean includeId) throws SQLException {
        ps.setString(1, supplier.getName());
        ps.setString(2, supplier.getPhone());
        ps.setString(3, supplier.getAddress());
        ps.setBoolean(4, supplier.isStatus());
        if (includeId) {
            ps.setInt(5, supplier.getSupplierId());
        }
    }

    private boolean existsByName(String sql, String name, Integer excludeId) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
