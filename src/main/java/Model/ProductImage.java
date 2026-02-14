package Model;



public class ProductImage {
    private int imageId;
    private int productId;
    private String image;   // url/path
    private boolean isMain;

    public ProductImage() {}

    public ProductImage(int imageId, int productId, String image, boolean isMain) {
        this.imageId = imageId;
        this.productId = productId;
        this.image = image;
        this.isMain = isMain;
    }

    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isMain() { return isMain; }
    public void setMain(boolean main) { isMain = main; }
}
