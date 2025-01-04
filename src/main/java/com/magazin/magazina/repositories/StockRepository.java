package com.magazin.magazina.repositories;

import com.magazin.magazina.models.Product;
import com.magazin.magazina.models.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Integer> {


    Optional<Stock> findByProductId(Integer productId);
}
