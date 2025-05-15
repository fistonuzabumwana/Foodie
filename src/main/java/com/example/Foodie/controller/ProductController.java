package com.example.Foodie.controller;

import com.example.Foodie.model.Product;
import com.example.Foodie.service.ProductService;
import com.example.Foodie.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products") // Base path for public product viewing
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;


    @Autowired
    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    // Display list of all products for customers
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list"; // Path to Thymeleaf template (e.g., templates/product-list.html)
    }

    // Display details of a single product
    @GetMapping("/view/{id}")
    public String viewProductDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "product-detail"; // Path to Thymeleaf template (e.g., templates/product-detail.html)
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
            return "redirect:/products";
        }
    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-detail";
        }
        return "redirect:/products";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getProductImage(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isPresent() && productOptional.get().getImageFileId() != null) {
            try {
                GridFsResource resource = fileStorageService.getResource(productOptional.get().getImageFileId());
                if (resource != null && resource.exists()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG) // Adjust based on actual content type
                            .header("Content-Disposition", "inline")
                            .body(new InputStreamResource(resource.getInputStream()));
                }
            } catch (IOException e) {
                // Log error
                return ResponseEntity.status(500).build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}