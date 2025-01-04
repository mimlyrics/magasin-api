package com.magazin.magazina.request;

public class OrderItemRequest {
    private Integer orderId;
    private Integer productId; // Change from `product` to `productId`
    private Integer quantity;

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() { // Updated getter
        return productId;
    }

    public void setProductId(Integer productId) { // Updated setter
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
