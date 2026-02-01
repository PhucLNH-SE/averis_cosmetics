package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {
    private int productId;
    private String name;
    private String description;
    private boolean status;

    private Brand brand;
    private Category category;
// Product.java
private String mainImage;
public String getMainImage() { return mainImage; }
public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    private List<ProductImage> images = new ArrayList<>();
    private List<ProductVariant> variants = new ArrayList<>();

    public Product() {}

    public Product(int productId, String name, String description, boolean status,
                   Brand brand, Category category) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.brand = brand;
        this.category = category;
    }

    // getters/setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) {
        this.images = (images != null) ? images : new ArrayList<>();
    }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) {
        this.variants = (variants != null) ? variants : new ArrayList<>();
    }
}
