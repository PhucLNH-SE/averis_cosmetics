package model;


import java.math.BigDecimal;

public class ProductVariant {
    private int variantId;
    private int productId;

    private String variantName;
    private String sku;
    private BigDecimal price;
    private int stock; // hoặc quantity tùy DB
    private boolean status;

    public ProductVariant() {}

    // getters/setters
    public int getVariantId() { return variantId; }
    public void setVariantId(int variantId) { this.variantId = variantId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
