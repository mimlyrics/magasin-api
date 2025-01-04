package com.magazin.magazina.repositories;

import com.magazin.magazina.models.Category;
import com.magazin.magazina.models.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository <Category, Integer> {

    List<Category> findByName(String parentName);
}
