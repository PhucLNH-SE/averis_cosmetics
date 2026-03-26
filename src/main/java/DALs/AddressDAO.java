package DALs;

import Utils.DBContext;
import Model.Address;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO extends DBContext {

    public List<Address> getAddressesByCustomerId(int customerId) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM Address WHERE customer_id = ? AND is_deleted = 0 ORDER BY is_default DESC, address_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return addresses;
    }

    public Address getAddressById(int addressId) {
        String sql = "SELECT * FROM Address WHERE address_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAddress(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean insertAddress(Address address) {
        String sql = "INSERT INTO Address (customer_id, receiver_name, phone, province, district, ward, street_address, is_default) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            int restoredAddressId = findDeletedDuplicateAddressId(address);
            if (restoredAddressId > 0) {
                if (restoreDeletedAddress(restoredAddressId, address)) {
                    address.setAddressId(restoredAddressId);
                    return true;
                }
                return false;
            }

            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, address.getCustomerId());
                ps.setString(2, address.getReceiverName());
                ps.setString(3, address.getPhone());
                ps.setString(4, address.getProvince());
                ps.setString(5, address.getDistrict());
                ps.setString(6, address.getWard());
                ps.setString(7, address.getStreetAddress());
                ps.setBoolean(8, address.getIsDefault() != null ? address.getIsDefault() : false);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            address.setAddressId(generatedKeys.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateAddress(Address address) {
        String sql = "UPDATE Address SET receiver_name = ?, phone = ?, province = ?, district = ?, ward = ?, street_address = ?, is_default = ? "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address.getReceiverName());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getProvince());
            ps.setString(4, address.getDistrict());
            ps.setString(5, address.getWard());
            ps.setString(6, address.getStreetAddress());
            ps.setBoolean(7, address.getIsDefault() != null ? address.getIsDefault() : false);
            ps.setInt(8, address.getAddressId());
            ps.setInt(9, address.getCustomerId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String deleteAddress(int addressId, int customerId) {
        String selectSql = "SELECT is_default FROM Address WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";
        String deleteSql = "UPDATE Address SET is_deleted = 1, is_default = 0 "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";
        String fallbackDefaultSql = "SELECT TOP 1 address_id FROM Address "
                + "WHERE customer_id = ? AND is_deleted = 0 ORDER BY is_default DESC, address_id ASC";
        String setDefaultSql = "UPDATE Address SET is_default = 1 WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try {
            connection.setAutoCommit(false);

            boolean wasDefault = false;
            try (PreparedStatement selectPs = connection.prepareStatement(selectSql)) {
                selectPs.setInt(1, addressId);
                selectPs.setInt(2, customerId);
                try (ResultSet rs = selectPs.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                        return "Address not found.";
                    }
                    wasDefault = rs.getBoolean("is_default");
                }
            }

            try (PreparedStatement deletePs = connection.prepareStatement(deleteSql)) {
                deletePs.setInt(1, addressId);
                deletePs.setInt(2, customerId);

                int rowsAffected = deletePs.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return "Failed to delete address.";
                }
            }

            if (wasDefault) {
                int replacementAddressId = 0;
                try (PreparedStatement fallbackPs = connection.prepareStatement(fallbackDefaultSql)) {
                    fallbackPs.setInt(1, customerId);
                    try (ResultSet rs = fallbackPs.executeQuery()) {
                        if (rs.next()) {
                            replacementAddressId = rs.getInt("address_id");
                        }
                    }
                }

                if (replacementAddressId > 0) {
                    try (PreparedStatement setDefaultPs = connection.prepareStatement(setDefaultSql)) {
                        setDefaultPs.setInt(1, replacementAddressId);
                        setDefaultPs.setInt(2, customerId);
                        setDefaultPs.executeUpdate();
                    }
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            return "success";
        } catch (Exception e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return "Failed to delete address.";
    }

    public boolean setDefaultAddress(int addressId, int customerId) {
        String sql1 = "UPDATE Address SET is_default = 0 WHERE customer_id = ? AND is_deleted = 0";
        String sql2 = "UPDATE Address SET is_default = 1 WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps1 = connection.prepareStatement(sql1)) {
                ps1.setInt(1, customerId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                ps2.setInt(1, addressId);
                ps2.setInt(2, customerId);

                int rows = ps2.executeUpdate();

                if (rows == 0) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return false;
    }

    private int findDeletedDuplicateAddressId(Address address) throws Exception {
        String sql = "SELECT TOP 1 address_id FROM Address "
                + "WHERE customer_id = ? AND receiver_name = ? AND phone = ? AND province = ? "
                + "AND district = ? AND ward = ? AND street_address = ? AND is_deleted = 1 "
                + "ORDER BY address_id DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, address.getCustomerId());
            ps.setString(2, address.getReceiverName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getProvince());
            ps.setString(5, address.getDistrict());
            ps.setString(6, address.getWard());
            ps.setString(7, address.getStreetAddress());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("address_id");
                }
            }
        }

        return 0;
    }

    private boolean restoreDeletedAddress(int addressId, Address address) throws Exception {
        String sql = "UPDATE Address SET receiver_name = ?, phone = ?, province = ?, district = ?, "
                + "ward = ?, street_address = ?, is_default = ?, is_deleted = 0 "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address.getReceiverName());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getProvince());
            ps.setString(4, address.getDistrict());
            ps.setString(5, address.getWard());
            ps.setString(6, address.getStreetAddress());
            ps.setBoolean(7, address.getIsDefault() != null ? address.getIsDefault() : false);
            ps.setInt(8, addressId);
            ps.setInt(9, address.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    private Address mapResultSetToAddress(ResultSet rs) throws Exception {
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

        return address;
    }
}
