package DALs;

import Model.Manager;
import Utils.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public boolean checkLogin(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }

        if (inputPassword.equals(storedPassword)) {
            return true;
        }

        try {
            return BCrypt.checkpw(inputPassword, storedPassword);
        } catch (Exception ex) {
            return false;
        }
    }
}
