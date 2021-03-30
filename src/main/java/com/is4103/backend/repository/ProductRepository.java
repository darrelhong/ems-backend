package com.is4103.backend.repository;

import java.util.List;

import com.is4103.backend.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p from Product p where p.businessPartner.id = ?1")
    public List<Product> findProductsByBusinessPartner(Long id);
}
