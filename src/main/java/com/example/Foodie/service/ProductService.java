package com.example.Foodie.service;

import com.example.Foodie.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product saveProduct(Product product, MultipartFile imageFile) throws IOException;
    void deleteProductById(Long id);
    // You can add more methods later, e.g., for searching or filtering
}