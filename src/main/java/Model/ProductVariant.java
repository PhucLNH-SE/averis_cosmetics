package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductVariant implements Serializable {
    private int variantId;
    private int productId;

    private String variantName;
    private String sku;
    private BigDecimal price;
    private int stock;
    private boolean status;

    // --- CÁC TRƯỜNG MỚI THÊM (để hiển thị trong Cart) ---
    private String productName; // Tên sản phẩm gốc (VD: Kem dưỡng ẩm A)
    private String imageUrl;    // Đường dẫn ảnh đại diện
    // ----------------------------------------------------

    public ProductVariant() {}

    // getters/setters cũ
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

    // --- GETTER/SETTER CHO TRƯỜNG MỚI ---
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}