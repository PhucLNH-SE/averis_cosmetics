/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Admin
 */

    public class OrderDetail implements Serializable {
    private int orderDetailId;       // order_detail_id (NOT NULL)
    private int orderId;             // order_id (NOT NULL)
    private int variantId;           // variant_id (NOT NULL)
    private int quantity;            // quantity (NOT NULL)
    private BigDecimal priceAtOrder; // price_at_order (NOT NULL) decimal(10,2)

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailId, int orderId, int variantId, int quantity, BigDecimal priceAtOrder) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
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
    
}
