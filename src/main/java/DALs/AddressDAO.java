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
        String sql = "INSERT INTO Address (customer_id, receiver_name, phone, province, district, ward, street_address, is_default) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";

        try {
            connection.setAutoCommit(false);

            int restoredAddressId = findDeletedDuplicateAddressId(address);
            if (restoredAddressId > 0) {
                if (!restoreDeletedAddress(restoredAddressId, address)) {
                    connection.rollback();
                    return false;
                }
                address.setAddressId(restoredAddressId);
            } else {
                try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, address.getCustomerId());
                    ps.setString(2, address.getReceiverName());
                    ps.setString(3, address.getPhone());
                    ps.setString(4, address.getProvince());
                    ps.setString(5, address.getDistrict());
                    ps.setString(6, address.getWard());
                    ps.setString(7, address.getStreetAddress());

                    if (ps.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }

                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            address.setAddressId(generatedKeys.getInt(1));
                        } else {
                            connection.rollback();
                            return false;
                        }
                    }
                }
            }

            if (Boolean.TRUE.equals(address.getIsDefault())
                    && !setDefaultAddressInternal(address.getAddressId(), address.getCustomerId())) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean updateAddress(Address address) {
        String sql = "UPDATE Address SET receiver_name = ?, phone = ?, province = ?, district = ?, ward = ?, street_address = ? "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, address.getReceiverName());
                ps.setString(2, address.getPhone());
                ps.setString(3, address.getProvince());
                ps.setString(4, address.getDistrict());
                ps.setString(5, address.getWard());
                ps.setString(6, address.getStreetAddress());
                ps.setInt(7, address.getAddressId());
                ps.setInt(8, address.getCustomerId());

                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            if (Boolean.TRUE.equals(address.getIsDefault())) {
                if (!setDefaultAddressInternal(address.getAddressId(), address.getCustomerId())) {
                    connection.rollback();
                    return false;
                }
            } else if (!clearDefaultAddressInternal(address.getAddressId(), address.getCustomerId())) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean deleteAddress(int addressId, int customerId) {
        String sql = "UPDATE Address SET is_deleted = 1, is_default = 0 "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setDefaultAddress(int addressId, int customerId) {
        try {
            connection.setAutoCommit(false);

            if (!setDefaultAddressInternal(addressId, customerId)) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                + "ward = ?, street_address = ?, is_default = 0, is_deleted = 0 "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address.getReceiverName());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getProvince());
            ps.setString(4, address.getDistrict());
            ps.setString(5, address.getWard());
            ps.setString(6, address.getStreetAddress());
            ps.setInt(7, addressId);
            ps.setInt(8, address.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    private boolean setDefaultAddressInternal(int addressId, int customerId) throws Exception {
        String resetSql = "UPDATE Address SET is_default = 0 WHERE customer_id = ? AND is_deleted = 0";
        String setSql = "UPDATE Address SET is_default = 1 WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try (PreparedStatement resetPs = connection.prepareStatement(resetSql)) {
            resetPs.setInt(1, customerId);
            resetPs.executeUpdate();
        }

        try (PreparedStatement setPs = connection.prepareStatement(setSql)) {
            setPs.setInt(1, addressId);
            setPs.setInt(2, customerId);
            return setPs.executeUpdate() > 0;
        }
    }

    private boolean clearDefaultAddressInternal(int addressId, int customerId) throws Exception {
        String sql = "UPDATE Address SET is_default = 0 "
                + "WHERE address_id = ? AND customer_id = ? AND is_deleted = 0";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressId);
            ps.setInt(2, customerId);
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
