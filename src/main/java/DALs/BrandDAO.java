package DALs;

import Model.Brand;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO extends DBContext {

    public List<Brand> getAll() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT brand_id, name, status FROM Brand ORDER BY brand_id ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Brand brand = new Brand();
                brand.setBrandId(rs.getInt("brand_id"));
                brand.setName(rs.getString("name"));
                brand.setStatus(rs.getBoolean("status"));
                brands.add(brand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return brands;
    }

    public Brand getById(int id) {
        String sql = "SELECT brand_id, name, status FROM Brand WHERE brand_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Brand brand = new Brand();
                    brand.setBrandId(rs.getInt("brand_id"));
                    brand.setName(rs.getString("name"));
                    brand.setStatus(rs.getBoolean("status"));
                    return brand;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Brand brand) {
        String sql = "INSERT INTO Brand (name, status) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, brand.getName());
            ps.setBoolean(2, brand.isStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Brand brand) {
        String sql = "UPDATE Brand SET name = ?, status = ? WHERE brand_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, brand.getName());
            ps.setBoolean(2, brand.isStatus());
            ps.setInt(3, brand.getBrandId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM Brand WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByNameExceptId(String name, int excludeId) {
        String sql = "SELECT 1 FROM Brand WHERE name = ? AND brand_id != ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
