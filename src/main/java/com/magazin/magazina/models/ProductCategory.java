package com.magazin.magazina.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="product_categories")
public class ProductCategory {

    @Id
    @SequenceGenerator(name="productCategory_id_sequence", sequenceName = "productCategory_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productCategory_id_sequence")
    private Integer id;
    private String name;
    private String imageUrl;

    public String getName() {
        return name;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id",referencedColumnName = "id", columnDefinition = "INTEGER")
    //@JsonManagedReference("category-Category")
    @JsonBackReference("category-Category")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @OneToMany(mappedBy = "productCategory")
    @JsonManagedReference("productCategory-product")
    private List <Product> products;

    public void setName(String name) {
        this.name = name;
    }

    private LocalDateTime createdAt;

    @Column(updatable = false)
    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
