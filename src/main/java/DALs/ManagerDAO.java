package DALs;

import Model.Manager;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class ManagerDAO extends DBContext {
// get managers
    public Manager getByEmail(String email) {
        String sql = "SELECT manager_id, full_name, email, password, manager_role, status "
                + "FROM Manager WHERE email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Manager manager = new Manager();
                    manager.setManagerId(rs.getInt("manager_id"));
                    manager.setFullName(rs.getString("full_name"));
                    manager.setEmail(rs.getString("email"));
                    manager.setPassword(rs.getString("password"));
                    manager.setManagerRole(rs.getString("manager_role"));
                    manager.setStatus(rs.getBoolean("status"));
                    return manager;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
// get Manager PhucLNH
    public Manager getById(int managerId) {
        String sql = "SELECT manager_id, full_name, email, password, manager_role, status "
                + "FROM Manager WHERE manager_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Manager manager = new Manager();
                    manager.setManagerId(rs.getInt("manager_id"));
                    manager.setFullName(rs.getString("full_name"));
                    manager.setEmail(rs.getString("email"));
                    manager.setPassword(rs.getString("password"));
                    manager.setManagerRole(rs.getString("manager_role"));
                    manager.setStatus(rs.getBoolean("status"));
                    return manager;
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
                Manager manager = new Manager();
                manager.setManagerId(rs.getInt("manager_id"));
                manager.setFullName(rs.getString("full_name"));
                manager.setEmail(rs.getString("email"));
                manager.setPassword(rs.getString("password"));
                manager.setManagerRole(rs.getString("manager_role"));
                manager.setStatus(rs.getBoolean("status"));
                list.add(manager);
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

    public boolean addManager(String fullName, String email, String password, String role, Boolean status) {
        String sql = "INSERT INTO Manager (full_name, email, password, manager_role, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, hashPassword(password));
            ps.setString(4, role);
            ps.setBoolean(5, status != null ? status : true);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateManager(int managerId, String fullName, String email, String role, Boolean status, String password) {
        boolean changePassword = password != null && !password.trim().isEmpty();
        if (changePassword) {
            String sql = "UPDATE Manager SET full_name = ?, email = ?, manager_role = ?, status = ?, password = ? "
                    + "WHERE manager_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, role);
                ps.setBoolean(4, status != null ? status : false);
                ps.setString(5, hashPassword(password));
                ps.setInt(6, managerId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String sql = "UPDATE Manager SET full_name = ?, email = ?, manager_role = ?, status = ? "
                    + "WHERE manager_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, role);
                ps.setBoolean(4, status != null ? status : false);
                ps.setInt(5, managerId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private String hashPassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
