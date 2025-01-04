package com.magazin.magazina.repositories;

import com.magazin.magazina.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository <Order, Integer> {

    boolean existsByCustomerId(Integer customerId);
}
