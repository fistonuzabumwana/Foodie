package com.example.Foodie.repository;

import com.example.Foodie.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // You can add custom query methods here if needed, e.g., findByNameContaining
}