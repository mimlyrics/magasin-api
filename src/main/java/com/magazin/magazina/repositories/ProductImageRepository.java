package com.magazin.magazina.repositories;

import com.magazin.magazina.models.Product;
import com.magazin.magazina.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {


    List <ProductImage> findByProduct(Product product);
}
