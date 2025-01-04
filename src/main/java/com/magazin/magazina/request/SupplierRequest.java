package com.magazin.magazina.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class SupplierRequest {
    private String name;
    private String address;
    private String taxId;
}
