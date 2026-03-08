/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;


import java.math.BigDecimal;

/**
 *
 * @author Admin
 */

    public class OrderDetail {
    private int orderDetailId;       // order_detail_id (NOT NULL)
    private int orderId;             // order_id (NOT NULL)
    private int variantId;           // variant_id (NOT NULL)
    private int quantity;            // quantity (NOT NULL)
    private BigDecimal priceAtOrder; // price_at_order (NOT NULL) decimal(10,2)
// thêm field
        public String ProductName ;
    public String ImageUrl ;
    public String BrandName;
    public String CategoryName ;
    public OrderDetail() {
    }

    public OrderDetail(int orderDetailId, int orderId, int variantId, int quantity, BigDecimal priceAtOrder, String ProductName, String ImageUrl, String BrandName, String CategoryName) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
        this.ProductName = ProductName;
        this.ImageUrl = ImageUrl;
        this.BrandName = BrandName;
        this.CategoryName = CategoryName;
    }

    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public BigDecimal getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(BigDecimal priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getBrandName() {
        return BrandName;
    }

    public void setBrandName(String BrandName) {
        this.BrandName = BrandName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    
}
