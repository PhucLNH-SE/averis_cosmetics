package Model;


import java.math.BigDecimal;

public class ProductVariant {
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
        private String categoryName;
        private BigDecimal importPrice;
    // ----------------------------------------------------

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public ProductVariant(int variantId, int productId, String variantName, String sku, BigDecimal price, int stock, boolean status, String productName, String imageUrl, String categoryName, BigDecimal importPrice) {
        this.variantId = variantId;
        this.productId = productId;
        this.variantName = variantName;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
        this.status = status;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.importPrice = importPrice;
    }

   

    public ProductVariant() {
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}