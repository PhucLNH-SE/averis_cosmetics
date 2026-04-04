
package Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ImportOrder {

    private int purchaseOrderId;
    private String importCode;
    private Integer supplierId;
    private int brandId;
    private int createdBy;
    private String invoiceNo;
    private String note;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String status;
    private LocalDateTime receivedAt;
    private Integer receivedBy;
    private List<ImportOrderDetail> details = new ArrayList<>();

    private String managerName;
    private String managerRole;
    private String receivedByName;
    private String brandName;
    private String supplierName;
    private String supplierPhone;
    private String supplierAddress;

    public ImportOrder() {
    }

    public ImportOrder(int purchaseOrderId, int brandId, int createdBy, BigDecimal totalAmount, LocalDateTime createdAt, String status, LocalDateTime receivedAt, Integer receivedBy, String managerName, String managerRole, String brandName) {
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

    public String getImportCode() {
        return importCode;
    }

    public void setImportCode(String importCode) {
        this.importCode = importCode;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getReceivedByName() {
        return receivedByName;
    }

    public void setReceivedByName(String receivedByName) {
        this.receivedByName = receivedByName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public List<ImportOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ImportOrderDetail> details) {
        this.details = details == null ? new ArrayList<>() : new ArrayList<>(details);
    }

    public void addDetail(ImportOrderDetail detail) {
        if (detail != null) {
            details.add(detail);
        }
    }

    public boolean hasDetails() {
        return details != null && !details.isEmpty();
    }

    public BigDecimal calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (details == null) {
            return total;
        }

        for (ImportOrderDetail detail : details) {
            if (detail != null) {
                total = total.add(detail.calculateSubtotal());
            }
        }
        return total;
    }
}
