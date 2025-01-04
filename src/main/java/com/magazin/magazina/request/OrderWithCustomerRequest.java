package com.magazin.magazina.request;

public class OrderWithCustomerRequest {
    private OrderRequest orderRequest;
    private CustomerRequest customerRequest;

    // Getters and setters
    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public CustomerRequest getCustomerRequest() {
        return customerRequest;
    }

    public void setCustomerRequest(CustomerRequest customerRequest) {
        this.customerRequest = customerRequest;
    }
}
