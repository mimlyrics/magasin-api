package com.magazin.magazina.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String address;
    private String taxId;

    @OneToOne(cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference("user-supplier")
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private LocalDateTime createdAt;

    @Column(updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "supplier", orphanRemoval = true)
    @JsonManagedReference("supplier-products")
    private List <Product> products;

    public Integer getId() {
        return id;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("taxId")
    public String getTaxId() {
        return taxId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
}
