/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.List;

/**
 *
 * @author Admin
 */
public class Product {
   int productId;
   String ngame;
   String description;
   boolean status;
   Brand brand;
   Category category;
           List<ProductImage> images;
           List<ProductVariant> variants;

    public Product() {
    }

    public Product(int productId, String ngame, String description, boolean status, Brand brand, Category category, List<ProductImage> images, List<ProductVariant> variants) {
        this.productId = productId;
        this.ngame = ngame;
        this.description = description;
        this.status = status;
        this.brand = brand;
        this.category = category;
        this.images = images;
        this.variants = variants;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getNgame() {
        return ngame;
    }

    public void setNgame(String ngame) {
        this.ngame = ngame;
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
        this.images = images;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }
           
}
