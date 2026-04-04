package DALs;

import Model.Category;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {

    public List<String> getActiveCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        String sql = "SELECT name FROM Category WHERE status = 1 ORDER BY name ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String categoryName = rs.getString("name");
                if (categoryName != null && !categoryName.trim().isEmpty()) {
                    categoryNames.add(categoryName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryNames;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, status FROM Category ORDER BY category_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(mapCategory(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT category_id, name, status FROM Category WHERE category_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertCategory(String name, boolean status) {
        String sql = "INSERT INTO Category(name, status) VALUES(?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setBoolean(2, status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCategory(int categoryId, String name, boolean status) {
        String sql = "UPDATE Category SET name = ?, status = ? WHERE category_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setBoolean(2, status);
            ps.setInt(3, categoryId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM Category WHERE name = ?";
        return existsByName(sql, name, null);
    }

    public boolean existsByNameExceptId(String name, int excludeId) {
        String sql = "SELECT 1 FROM Category WHERE name = ? AND category_id <> ?";
        return existsByName(sql, name, excludeId);
    }

    private Category mapCategory(ResultSet rs) throws Exception {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        category.setStatus(rs.getBoolean("status"));
        return category;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
