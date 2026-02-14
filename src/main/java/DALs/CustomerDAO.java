package DALs;

import Utils.DBContext;
import Model.Customer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerDAO extends DBContext {

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
     public boolean updateProfile(Customer c) {
        String sql =
            "UPDATE Customers " +
            "SET username = ?, full_name = ?, email = ?, gender = ?, date_of_birth = ? " +
            "WHERE customer_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            // NOT NULL theo table
            ps.setString(1, c.getUsername());
            ps.setString(2, c.getFullName());

            // email NULL được
            if (c.getEmail() == null || c.getEmail().trim().isEmpty()) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setString(3, c.getEmail().trim());
            }

            // gender NULL được (varchar)
            if (c.getGender() == null || c.getGender().trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, c.getGender().trim());
            }

            // date_of_birth NULL được (date) - LocalDate
            LocalDate dob = c.getDateOfBirth();
            if (dob == null) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(dob));
            }

            ps.setInt(6, c.getCustomerId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}