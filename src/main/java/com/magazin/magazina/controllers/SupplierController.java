package com.magazin.magazina.controllers;

import com.magazin.magazina.models.Role;
import com.magazin.magazina.models.Supplier;
import com.magazin.magazina.models.User;
import com.magazin.magazina.repositories.SupplierRepository;
import com.magazin.magazina.repositories.UserRepository;
import com.magazin.magazina.request.*;
import com.magazin.magazina.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(
        origins = {
                "https://magazina.onrender.com",
                "http://localhost:3000"
        }, // Multiple allowed origins
        allowedHeaders = "*", // Allow all headers
        methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.OPTIONS}, // Allowed methods
        allowCredentials = "true" // Allow credentials like cookies
)
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SupplierRepository supplierRepository;


    @PostMapping
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        return supplierService.createSupplier(supplier);
    }

    @GetMapping
    public List <Supplier> getSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public Supplier getSupplier(@PathVariable Integer id) {
        return supplierService.getSupplierById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteSupplierById(id);
    }

    @PutMapping("/promote/{userId}")
    public ResponseEntity<?> promoteToSupplier(
            @PathVariable Integer userId,
            @RequestBody Supplier supplierRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        if (user.getRole() == Role.SUPPLIER) {
            return ResponseEntity.badRequest().body("User is already a supplier");
        }

        user.setRole(Role.SUPPLIER);

        Supplier supplier = new Supplier();
        supplier.setUser(user);
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setName(supplierRequest.getName());
        supplier.setAddress(supplierRequest.getAddress());
        supplier.setTaxId(supplierRequest.getTaxId());

        supplierRepository.save(supplier);
        userRepository.save(user);

        return ResponseEntity.ok("User promoted to supplier successfully");
    }

}
