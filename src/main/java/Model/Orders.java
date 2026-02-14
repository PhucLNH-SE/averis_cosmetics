/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class Orders {
    private int orderId;                
    private int customerId;              
    private int addressId;              

    private Integer handledBy;          
    private Integer voucherId;          
    private BigDecimal discountAmount;   

    private String paymentMethod;       
    private String paymentStatus;        
    private String orderStatus;         

    private BigDecimal totalAmount;      

    private LocalDateTime createdAt;     
    private LocalDateTime paidAt;  

    public Orders() {
    }

    public Orders(int orderId, int customerId, int addressId, Integer handledBy, Integer voucherId, BigDecimal discountAmount, String paymentMethod, String paymentStatus, String orderStatus, BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime paidAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.addressId = addressId;
        this.handledBy = handledBy;
        this.voucherId = voucherId;
        this.discountAmount = discountAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public Integer getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(Integer handledBy) {
        this.handledBy = handledBy;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
    
}
