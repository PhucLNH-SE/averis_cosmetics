package model;

import java.io.Serializable;

public class Category implements Serializable {
    private int categoryId;
    private String name;
    private boolean status;

    public Category() {}

    public Category(int categoryId, String name, boolean status) {
        this.categoryId = categoryId;
        this.name = name;
        this.status = status;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
