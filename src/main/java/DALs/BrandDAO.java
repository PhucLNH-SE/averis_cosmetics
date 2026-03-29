package DALs;

import Model.Brand;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO extends DBContext {

    public List<Brand> getAllBrands() {
        List<Brand> brands = new ArrayList<>();
        String sql = "SELECT brand_id, name, status FROM Brand ORDER BY brand_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                brands.add(mapBrand(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return brands;
    }

    public Brand getBrandById(int brandId) {
        String sql = "SELECT brand_id, name, status FROM Brand WHERE brand_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, brandId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBrand(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertBrand(Brand brand) {
        String sql = "INSERT INTO Brand (name, status) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillBrandStatement(ps, brand, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateBrand(Brand brand) {
        String sql = "UPDATE Brand SET name = ?, status = ? WHERE brand_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            fillBrandStatement(ps, brand, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM Brand WHERE name = ?";
        return existsByName(sql, name, null);
    }

    public boolean existsByNameExceptId(String name, int excludeId) {
        String sql = "SELECT 1 FROM Brand WHERE name = ? AND brand_id != ?";
        return existsByName(sql, name, excludeId);
    }

    private Brand mapBrand(ResultSet rs) throws SQLException {
        Brand brand = new Brand();
        brand.setBrandId(rs.getInt("brand_id"));
        brand.setName(rs.getString("name"));
        brand.setStatus(rs.getBoolean("status"));
        return brand;
    }

    private void fillBrandStatement(PreparedStatement ps, Brand brand, boolean includeId) throws SQLException {
        ps.setString(1, brand.getName());
        ps.setBoolean(2, brand.isStatus());
        if (includeId) {
            ps.setInt(3, brand.getBrandId());
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
