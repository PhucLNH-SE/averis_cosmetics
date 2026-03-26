
package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseOrder {

    private int purchaseOrderId;
    private int brandId;
    private int createdBy;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String status;
    private LocalDateTime receivedAt;
    private Integer receivedBy;

    private String managerName;
private String managerRole;
       private String brandName;
    public PurchaseOrder() {
    }

    public PurchaseOrder(int purchaseOrderId, int brandId, int createdBy, BigDecimal totalAmount, LocalDateTime createdAt, String status, LocalDateTime receivedAt, Integer receivedBy, String managerName, String managerRole, String brandName) {
        this.purchaseOrderId = purchaseOrderId;
        this.brandId = brandId;
        this.createdBy = createdBy;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.status = status;
        this.receivedAt = receivedAt;
        this.receivedBy = receivedBy;
        this.managerName = managerName;
        this.managerRole = managerRole;
        this.brandName = brandName;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Integer getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(Integer receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerRole() {
        return managerRole;
    }

    public void setManagerRole(String managerRole) {
        this.managerRole = managerRole;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    

   
}
