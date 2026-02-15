package DALs;

import Model.Address;
import Utils.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    private Connection con;

    public AddressDAO() {
        try {
            con = new DBContext().connection;
            if (con == null) {
                throw new RuntimeException("Failed to establish database connection");
            }
        } catch (Exception e) {
            System.err.println("Error initializing AddressDAO: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }

    public void addAddress(Address a) {
        String sql = "INSERT INTO Address "
                   + "(customer_id, receiver_name, phone, "
                   + "province, district, ward, street_address, is_default) "
                   + "VALUES (?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, a.getCustomerId());
            ps.setString(2, a.getReceiverName());
            ps.setString(3, a.getPhone());
            ps.setString(4, a.getProvince());
            ps.setString(5, a.getDistrict());
            ps.setString(6, a.getWard());
            ps.setString(7, a.getStreetAddress());
            ps.setBoolean(8, a.isIsDefault());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Address added successfully for customer ID: " + a.getCustomerId());
            }
        } catch (Exception e) {
            System.err.println("Error adding address: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add address", e);
        }
    }

    public List<Address> getAddressesByCustomerId(int customerId) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM Address WHERE customer_id = ? ORDER BY is_default DESC, address_id ASC";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, customerId);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Address address = new Address();
                address.setAddressId(rs.getInt("address_id"));
                address.setCustomerId(rs.getInt("customer_id"));
                address.setReceiverName(rs.getString("receiver_name"));
                address.setPhone(rs.getString("phone"));
                address.setProvince(rs.getString("province"));
                address.setDistrict(rs.getString("district"));
                address.setWard(rs.getString("ward"));
                address.setStreetAddress(rs.getString("street_address"));
                address.setIsDefault(rs.getBoolean("is_default"));
                
                addresses.add(address);
            }
            
            System.out.println("Retrieved " + addresses.size() + " addresses for customer ID: " + customerId);
        } catch (Exception e) {
            System.err.println("Error retrieving addresses: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve addresses", e);
        }
        
        return addresses;
    }
}