package DALs;

import Model.Supplier;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO extends DBContext {

    public boolean insert(Supplier supplier) {
        String sql = "INSERT INTO Supplier (name, phone, address, status) VALUES (?, ?, ?, 1)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getPhone());
            ps.setString(3, supplier.getAddress());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setName(rs.getString("name"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setAddress(rs.getString("address"));
                supplier.setStatus(rs.getBoolean("status"));
                suppliers.add(supplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return suppliers;
    }
}
