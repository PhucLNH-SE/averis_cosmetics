/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    // Các trường phục vụ chức năng Feedback
    private Integer rating; 
    private String reviewComment;
    private LocalDateTime reviewedAt;
    // Thêm các biến này bên dưới các biến review hiện tại
    private Integer managerResponse;
    private String responseContent;
    private LocalDateTime respondedAt;
    // --- CÁC BIẾN PHỤ HIỂN THỊ CHO STAFF ---
    private String customerName;
    private String managerName;
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
    
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    // --- GETTER & SETTER ---
    public Integer getManagerResponse() {
        return managerResponse;
    }

    public void setManagerResponse(Integer managerResponse) {
        this.managerResponse = managerResponse;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
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
    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
}
