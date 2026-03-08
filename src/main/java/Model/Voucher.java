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
public class Voucher {
    private int voucherId;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private int quantity;
    private LocalDateTime expiredAt;
    private Boolean status;

    private String voucherType;
    private LocalDateTime fixedStartAt;
    private LocalDateTime fixedEndAt;
    private Integer relativeDays;
    private int claimedQuantity;
    private LocalDateTime createdAt;

    public Voucher() {
    }

    public Voucher(int voucherId, String code, String discountType, BigDecimal discountValue, int quantity,
            LocalDateTime expiredAt, Boolean status, String voucherType, LocalDateTime fixedStartAt,
            LocalDateTime fixedEndAt, Integer relativeDays, int claimedQuantity, LocalDateTime createdAt) {
        this.voucherId = voucherId;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.quantity = quantity;
        this.expiredAt = expiredAt;
        this.status = status;
        this.voucherType = voucherType;
        this.fixedStartAt = fixedStartAt;
        this.fixedEndAt = fixedEndAt;
        this.relativeDays = relativeDays;
        this.claimedQuantity = claimedQuantity;
        this.createdAt = createdAt;
    }

    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public LocalDateTime getFixedStartAt() {
        return fixedStartAt;
    }

    public void setFixedStartAt(LocalDateTime fixedStartAt) {
        this.fixedStartAt = fixedStartAt;
    }

    public LocalDateTime getFixedEndAt() {
        return fixedEndAt;
    }

    public void setFixedEndAt(LocalDateTime fixedEndAt) {
        this.fixedEndAt = fixedEndAt;
    }

    public Integer getRelativeDays() {
        return relativeDays;
    }

    public void setRelativeDays(Integer relativeDays) {
        this.relativeDays = relativeDays;
    }

    public int getClaimedQuantity() {
        return claimedQuantity;
    }

    public void setClaimedQuantity(int claimedQuantity) {
        this.claimedQuantity = claimedQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
