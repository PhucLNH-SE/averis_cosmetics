package DALs;

import Model.Manager;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class ManagerDAO extends DBContext {

    public Manager getByEmail(String email) {
        String sql = "SELECT manager_id, full_name, email, password, manager_role, status "
                + "FROM Manager WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapManager(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Manager getById(int managerId) {
        String sql = "SELECT manager_id, full_name, email, password, manager_role, status "
                + "FROM Manager WHERE manager_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapManager(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Manager> getAllStaff() {
        List<Manager> list = new ArrayList<>();
        String sql = "SELECT manager_id, full_name, email, password, manager_role, status "
                + "FROM Manager WHERE manager_role = 'STAFF' ORDER BY manager_id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapManager(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean isEmailExist(String email, int excludeId) {
        String sql = "SELECT 1 FROM Manager WHERE email = ? AND manager_id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isNameExist(String name, int excludeId) {
        String sql = "SELECT 1 FROM Manager WHERE full_name = ? AND manager_id != ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addManager(Manager manager) {
        String sql = "INSERT INTO Manager (full_name, email, password, manager_role, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, manager.getFullName());
            ps.setString(2, manager.getEmail());
            ps.setString(3, hashPassword(manager.getPassword()));
            ps.setString(4, manager.getManagerRole());
            ps.setBoolean(5, resolveManagerStatus(manager.getStatus(), true));
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateManager(Manager manager) {
        boolean changePassword = manager.getPassword() != null && !manager.getPassword().trim().isEmpty();
        String sql = buildUpdateManagerSql(changePassword);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, manager.getFullName());
            ps.setString(2, manager.getEmail());
            ps.setString(3, manager.getManagerRole());
            ps.setBoolean(4, resolveManagerStatus(manager.getStatus(), false));

            int paramIndex = 5;
            if (changePassword) {
                ps.setString(paramIndex++, hashPassword(manager.getPassword()));
            }

            ps.setInt(paramIndex, manager.getManagerId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean banManager(int id) {
        String sql = "UPDATE Manager SET status = 0 WHERE manager_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unbanManager(int id) {
        String sql = "UPDATE Manager SET status = 1 WHERE manager_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private Manager mapManager(ResultSet rs) throws Exception {
        Manager manager = new Manager();
        manager.setManagerId(rs.getInt("manager_id"));
        manager.setFullName(rs.getString("full_name"));
        manager.setEmail(rs.getString("email"));
        manager.setPassword(rs.getString("password"));
        manager.setManagerRole(rs.getString("manager_role"));
        manager.setStatus(rs.getBoolean("status"));
        return manager;
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    private boolean resolveManagerStatus(Boolean status, boolean defaultValue) {
        return status != null ? status : defaultValue;
    }

    private String buildUpdateManagerSql(boolean changePassword) {
        StringBuilder sql = new StringBuilder("UPDATE Manager SET full_name = ?, email = ?, manager_role = ?, status = ?");
        if (changePassword) {
            sql.append(", password = ?");
        }
        sql.append(" WHERE manager_id = ?");
        return sql.toString();
    }
}
