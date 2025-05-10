package com.example.Foodie.controller.admin; // Or your chosen package

import com.example.Foodie.model.Product;
import com.example.Foodie.service.ProductService;
import jakarta.validation.Valid; // For Spring Boot 3.x
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/products") // Base path for all admin product operations
public class AdminProductController {

    private final ProductService productService;

    @Autowired
    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // Display list of all products for admin
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products-list"; // Path to Thymeleaf template
    }

    // Show form to add a new product
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("pageTitle", "Add New Product");
        return "admin/product-form"; // Path to Thymeleaf template for add/edit form
    }

    // Process the form for adding/updating a product
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult bindingResult, // For validation results
                              RedirectAttributes redirectAttributes,
                              Model model) {

        if (bindingResult.hasErrors()) {
            // If validation errors, return to the form and display them
            model.addAttribute("pageTitle", product.getId() == null ? "Add New Product" : "Edit Product");
            return "admin/product-form";
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product saved successfully!");
        return "redirect:/admin/products"; // Redirect to the product list
    }

    // Show form to edit an existing product
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("pageTitle", "Edit Product");
                    return "admin/product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
                    return "redirect:/admin/products";
                });
    }

    // Delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        } catch (Exception e) { // Catch potential errors, e.g., if product can't be deleted due to constraints
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }
}