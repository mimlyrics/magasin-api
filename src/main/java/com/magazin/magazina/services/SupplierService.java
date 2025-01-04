package com.magazin.magazina.services;


import com.magazin.magazina.models.Supplier;
import com.magazin.magazina.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public Supplier createSupplier(Supplier supplier) {
        Supplier supplier1 = new Supplier();
        supplier1.setTaxId(supplier1.getTaxId());
        supplier1.setAddress(supplier1.getAddress());
        return supplierRepository.save(supplier1);
    }

    public void deleteSupplierById(Integer id) {
        supplierRepository.deleteById(id);
    }

    public List <Supplier> getAllSuppliers() {
        return supplierRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Supplier getSupplierById(Integer id) {
        return supplierRepository.findById(id).orElseThrow();
    }

    public Supplier updateSupplier(Integer id, Supplier supplier) {
        /*supplier = supplierRepository.findById(id).orElseThrow();
        if(supplier != null) {
            Supplier supplier1 = new Supplier();
            supplier1.setAddress(supplier.getAddress());
            supplier1.setCode(supplier1.getCode());

            return supplierRepository.save(supplier1);
        }*/
        return null;
    }

}
