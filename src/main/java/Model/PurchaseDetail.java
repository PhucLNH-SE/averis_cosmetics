
package Model;

import java.math.BigDecimal;

public class PurchaseDetail {

  private int purchaseDetailId;
    private int purchaseOrderId;
    private int variantId;
    private int quantity;
    private BigDecimal importPrice;
    private Integer receivedQuantity;

    private String productName;
    private String variantName;

    public PurchaseDetail() {
    }

    public PurchaseDetail(int purchaseDetailId, int purchaseOrderId, int variantId, int quantity, BigDecimal importPrice, Integer receivedQuantity, String productName, String variantName) {
        this.purchaseDetailId = purchaseDetailId;
        this.purchaseOrderId = purchaseOrderId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.importPrice = importPrice;
        this.receivedQuantity = receivedQuantity;
        this.productName = productName;
        this.variantName = variantName;
    }

    public int getPurchaseDetailId() {
        return purchaseDetailId;
    }

    public void setPurchaseDetailId(int purchaseDetailId) {
        this.purchaseDetailId = purchaseDetailId;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public Integer getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Integer receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }
    
}
