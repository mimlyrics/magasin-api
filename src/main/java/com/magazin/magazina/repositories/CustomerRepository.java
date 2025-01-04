package com.magazin.magazina.repositories;

import com.magazin.magazina.models.Customer;
import com.magazin.magazina.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByUser(User user);
}
