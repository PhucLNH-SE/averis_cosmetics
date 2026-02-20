package model;

import java.math.BigDecimal;
import Model.ProductVariant;

public class CartItem {

    private ProductVariant variant;
    private int quantity;

    public CartItem(ProductVariant variant, int quantity) {
        this.variant = variant;
        this.quantity = quantity;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return variant.getPrice().multiply(new BigDecimal(quantity));
    }
}
