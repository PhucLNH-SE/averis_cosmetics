package model;



public class Brand {
    private int brandId;
    private String name;
    private boolean status;

    public Brand() {}

    public Brand(int brandId, String name, boolean status) {
        this.brandId = brandId;
        this.name = name;
        this.status = status;
    }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
