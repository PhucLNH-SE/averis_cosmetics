package DALs;

import Utils.DBContext;
import Model.Customer;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class CustomerDAO extends DBContext {

    private static final String TYPE_EMAIL_VERIFY = "EMAIL_VERIFY";

    public Customer getCustomerByUsername(String username) {
        String sql = "SELECT * FROM Customers WHERE username = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public Customer getCustomerByEmail(String email) {
        String sql = "SELECT * FROM Customers WHERE email = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public boolean insertCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (username, full_name, email, password, gender, date_of_birth, status, email_verified) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPassword());
            ps.setString(5, customer.getGender());
            ps.setObject(6, customer.getDateOfBirth());
            ps.setBoolean(7, customer.getStatus());
            ps.setBoolean(8, customer.getEmailVerified());

            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customers SET username = ?, full_name = ?, email = ?, password = ?, gender = ?, date_of_birth = ?, status = ?, email_verified = ? WHERE customer_id = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPassword());
            ps.setString(5, customer.getGender());
            ps.setObject(6, customer.getDateOfBirth());
            ps.setBoolean(7, customer.getStatus());
            ps.setBoolean(8, customer.getEmailVerified());
            ps.setInt(9, customer.getCustomerId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setUsername(rs.getString("username"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPassword(rs.getString("password"));
        customer.setGender(rs.getString("gender"));
        customer.setDateOfBirth(rs.getObject("date_of_birth", LocalDate.class));
        customer.setStatus(rs.getBoolean("status"));
        customer.setEmailVerified(rs.getBoolean("email_verified"));
        customer.setAuthToken(rs.getString("auth_token"));
        customer.setAuthTokenType(rs.getString("auth_token_type"));
        customer.setAuthTokenExpiredAt(rs.getObject("auth_token_expired_at", LocalDateTime.class));
        customer.setAuthTokenUsed(rs.getBoolean("auth_token_used"));

        return customer;
    }
    
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE username = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Customers WHERE email = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public Customer getCustomerByAuthToken(String token, String type) {
        if (token == null || type == null) return null;
        String sql = "SELECT * FROM Customers WHERE auth_token = ? AND auth_token_type = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateAuthTokenForVerification(int customerId, String token, java.time.LocalDateTime expiredAt) {
        String sql = "UPDATE Customers SET auth_token = ?, auth_token_type = ?, auth_token_expired_at = ?, auth_token_used = 0 WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setString(2, TYPE_EMAIL_VERIFY);
            ps.setObject(3, expiredAt);
            ps.setInt(4, customerId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEmailVerified(int customerId) {
        String sql = "UPDATE Customers SET email_verified = 1, auth_token_used = 1, auth_token = NULL, auth_token_type = NULL, auth_token_expired_at = NULL WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE customer_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
      public boolean updateProfile(Customer customer) {

    String sql =
        "UPDATE Customers " +
        "SET full_name = ?, email = ?, gender = ?, date_of_birth = ? " +
        "WHERE customer_id = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {

        // full_name (NOT NULL)
        ps.setString(1, customer.getFullName());

        // email (NULL được)
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            ps.setNull(2, Types.NVARCHAR);
        } else {
            ps.setString(2, customer.getEmail().trim());
        }

        // gender (NULL được)
        if (customer.getGender() == null || customer.getGender().trim().isEmpty()) {
            ps.setNull(3, Types.VARCHAR);
        } else {
            ps.setString(3, customer.getGender().trim());
        }

        // date_of_birth (NULL được)
        LocalDate dob = customer.getDateOfBirth();
        if (dob == null) {
            ps.setNull(4, Types.DATE);
        } else {
            ps.setDate(4, Date.valueOf(dob));
        }

        // WHERE
        ps.setInt(5, customer.getCustomerId());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}