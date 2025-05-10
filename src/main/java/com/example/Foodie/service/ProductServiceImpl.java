package com.example.Foodie.service;

import com.example.Foodie.model.Product;
import com.example.Foodie.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional // Default readOnly is false, so it allows writes
    public Product saveProduct(Product product) {
        // Add any business logic before saving, e.g., validation, setting default values
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        // Add any business logic before deleting, e.g., checking if product is in active orders
        productRepository.deleteById(id);
    }
}