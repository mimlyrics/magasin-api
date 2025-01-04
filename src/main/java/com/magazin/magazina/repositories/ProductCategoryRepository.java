package com.magazin.magazina.repositories;

import com.magazin.magazina.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository <ProductCategory, Integer> {

    //Optional<ProductCategory> findByParentCategoryId(Integer parentCategoryId);
}