package com.magazin.magazina.services;

import com.magazin.magazina.models.Customer;
import com.magazin.magazina.models.OrderItem;
import com.magazin.magazina.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List <Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Integer id) {
        return Optional.ofNullable(customerRepository.findById(id).orElse(null));
    }

}
