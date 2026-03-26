package Model;

public class ProductFeedbackSummary {
    private int productId;
    private String productName;
    private String productImageUrl;
    private double averageRating;
    private int totalFeedbacks;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalFeedbacks() { return totalFeedbacks; }
    public void setTotalFeedbacks(int totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }
}