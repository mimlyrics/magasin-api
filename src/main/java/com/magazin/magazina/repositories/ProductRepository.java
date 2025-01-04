package com.magazin.magazina.repositories;


import com.magazin.magazina.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository <Product, Integer> {

    List <Product> findByProductCategory_Name(String category);
}
