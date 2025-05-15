package com.example.Foodie.service;

import com.example.Foodie.model.Product;
import com.example.Foodie.repository.ProductRepository;
import com.example.Foodie.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;


    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
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
    public Product saveProduct(Product product, MultipartFile imageFile) throws IOException {
        // Add any business logic before saving, e.g., validation, setting default values
        if (imageFile != null && !imageFile.isEmpty()) {
            // Handle new image upload
            String oldFileId = product.getImageFileId();
            String newFileId = fileStorageService.store(imageFile);
            product.setImageFileId(newFileId);

            // Delete old image if it exists and is different from new one
            if (oldFileId != null && !oldFileId.equals(newFileId)) {
                try {
                    fileStorageService.delete(oldFileId);
                } catch (Exception e) {
                    // Log error but don't fail the operation
                    System.err.println("Failed to delete old product image: " + oldFileId);
                    e.printStackTrace();
                }
            }
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        // Add any business logic before deleting, e.g., checking if product is in active orders
        productRepository.deleteById(id);
    }
}