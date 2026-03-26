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
                Manager m = new Manager();
                m.setManagerId(rs.getInt("manager_id"));
                m.setFullName(rs.getString("full_name"));
                m.setEmail(rs.getString("email"));
                m.setPassword(rs.getString("password"));
                m.setManagerRole(rs.getString("manager_role"));
                m.setStatus(rs.getBoolean("status"));
                list.add(m);
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


    public boolean addManager(Manager m) {
        String sql = "INSERT INTO Manager (full_name, email, password, manager_role, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getFullName());
            ps.setString(2, m.getEmail());
            

            String hashedPassword = BCrypt.hashpw(m.getPassword(), BCrypt.gensalt());
            ps.setString(3, hashedPassword); 
            
            ps.setString(4, m.getManagerRole());
            

            boolean isActive = m.getStatus() != null ? m.getStatus() : true; // Mặc định là true nếu null
            ps.setBoolean(5, isActive);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateManager(Manager m) {

        boolean changePassword = (m.getPassword() != null && !m.getPassword().trim().isEmpty());
        
        StringBuilder sql = new StringBuilder("UPDATE Manager SET full_name = ?, email = ?, manager_role = ?, status = ?");
        if (changePassword) {
            sql.append(", password = ?");
        }
        sql.append(" WHERE manager_id = ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setString(1, m.getFullName());
            ps.setString(2, m.getEmail());
            ps.setString(3, m.getManagerRole());
            
            boolean isActive = m.getStatus() != null ? m.getStatus() : false;
            ps.setBoolean(4, isActive);
            
            int paramIndex = 5;

            if (changePassword) {
                String hashedPassword = BCrypt.hashpw(m.getPassword(), BCrypt.gensalt());
                ps.setString(paramIndex++, hashedPassword);
            }

            ps.setInt(paramIndex, m.getManagerId());
            
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
}
