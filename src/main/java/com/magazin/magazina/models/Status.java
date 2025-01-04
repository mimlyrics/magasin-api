package com.magazin.magazina.models;

public enum Status {
    IN_STOCK("In Stock"),          // Product is available for sale
    OUT_OF_STOCK("Out of Stock"),  // Product is currently unavailable
    DISCONTINUED("Discontinued"),  // Product is no longer available or will not be restocked
    PRE_ORDER("Pre-order"),        // Product is available for pre-order before release
    BACK_ORDER("Back-order"),      // Product is out of stock, but customers can still place orders
    LIMITED_AVAILABILITY("Limited Availability"), // Product is available but with very limited quantity
    PENDING("Pending"),            // Product status is pending (e.g., awaiting supplier shipment or approval)
    DAMAGED("Damaged"),            // Product is damaged and not available for sale
    RETIRED("Retired");            // Product has been retired, possibly for older versions of a product line

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
