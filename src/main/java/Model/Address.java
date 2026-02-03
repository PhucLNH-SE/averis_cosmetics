/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;



/**
 *
 * @author Admin
 */
public class Address {
     private int addressId;        // address_id (NOT NULL)
    private int customerId;       // customer_id (NOT NULL)
    private String receiverName;  // receiver_name (NOT NULL)
    private String phone;         // phone (NOT NULL)
    private String province;      // province (NOT NULL)
    private String district;      // district (NOT NULL)
    private String ward;          // ward (NOT NULL)
    private String streetAddress; // street_address (NOT NULL)
    private Boolean isDefault;    // is_default (NULL allowed)

    public Address() {
    }

    public Address(int addressId, int customerId, String receiverName, String phone, String province, String district, String ward, String streetAddress, Boolean isDefault) {
        this.addressId = addressId;
        this.customerId = customerId;
        this.receiverName = receiverName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.streetAddress = streetAddress;
        this.isDefault = isDefault;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
}
