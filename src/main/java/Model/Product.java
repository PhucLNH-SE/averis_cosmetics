package Model;

import java.util.ArrayList;
import java.util.List;
import Model.Brand;
import Model.Category;
import Model.ProductImage;
import Model.ProductVariant;

public class Product {

    private int productId;
    private String name;
    private String description;
    private boolean status;
    private double price;
    private double maxPrice;
    private Brand brand;
    private Category category;
    private String mainImage;
    private List<ProductImage> images = new ArrayList<>();
    private List<ProductVariant> variants = new ArrayList<>();

    public Product() {}

    // --- [SỬA] Cập nhật constructor cũ (vẫn giữ để không lỗi class khác) ---
    public Product(int productId, String name, String description, boolean status,
                   Brand brand, Category category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.brand = brand;
        this.category = category;
    }

    public Product(int productId, String name, String description, double price, double maxPrice, boolean status,
                   Brand brand, Category category, String mainImage) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.maxPrice = maxPrice;
        this.status = status;
        this.brand = brand;
        this.category = category;
        this.mainImage = mainImage;
    }

    // --- [THÊM MỚI] Getter/Setter cho Price ---
    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // getters/setters cho các trường khác
    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = (images != null) ? images : new ArrayList<>();
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = (variants != null) ? variants : new ArrayList<>();
    }

    public int getTotalStock() {
        int totalStock = 0;
        if (variants == null) {
            return 0;
        }

        for (ProductVariant variant : variants) {
            if (variant != null && variant.isStatus()) {
                totalStock += variant.getStock();
            }
        }

        return totalStock;
    }
}
