/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;



/**
 *
 * @author Admin
 */
public class CartDetail {
    private int cartDetailId;  // cart_detail_id
    private int customerId;    // customer_id
    private int variantId;     // variant_id
    private int quantity;

    public CartDetail(int cartDetailId, int customerId, int variantId, int quantity) {
        this.cartDetailId = cartDetailId;
        this.customerId = customerId;
        this.variantId = variantId;
        this.quantity = quantity;
    }

    public CartDetail() {
    }

    public int getCartDetailId() {
        return cartDetailId;
    }

    public void setCartDetailId(int cartDetailId) {
        this.cartDetailId = cartDetailId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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
    
}
