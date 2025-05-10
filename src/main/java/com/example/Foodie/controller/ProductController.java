package com.example.Foodie.controller;

import com.example.Foodie.model.Product;
import com.example.Foodie.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products") // Base path for public product viewing
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
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
}