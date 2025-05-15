package com.example.Foodie.controller.admin; // Or your chosen package

import com.example.Foodie.model.Product;
import com.example.Foodie.service.ProductService;
import com.example.Foodie.service.storage.FileStorageService;
import jakarta.validation.Valid; // For Spring Boot 3.x
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;


@Controller
@RequestMapping("/admin/products") // Base path for all admin product operations
public class AdminProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;


    @Autowired
    public AdminProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
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
    public String saveProduct(@Valid @ModelAttribute("product") Product product, @RequestParam("image") MultipartFile imageFile,
                              BindingResult bindingResult, // For validation results
                              RedirectAttributes redirectAttributes,
                              Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            // If validation errors, return to the form and display them
            model.addAttribute("pageTitle", product.getId() == null ? "Add New Product" : "Edit Product");
            return "admin/product-form";
        }

        try {
            productService.saveProduct(product, imageFile);
            redirectAttributes.addFlashAttribute("message", "Product has been saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving product: " + e.getMessage());
        }
        return "redirect:/admin/products"; // Redirect to the product list
    }

//    // Show form to edit an existing product
//    @GetMapping("/edit/{id}")
//    public String showEditProductForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
//        return productService.getProductById(id)
//                .map(product -> {
//                    model.addAttribute("product", product);
//                    model.addAttribute("pageTitle", "Edit Product");
//                    return "admin/product-form";
//                })
//                .orElseGet(() -> {
//                    redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
//                    return "redirect:/admin/products";
//                });
//    }

//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
//        Optional<Product> product = productService.getProductById(id);
//        if (product.isPresent()) {
//            model.addAttribute("product", product.get());
//            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
//            return "admin/product-edit";
//        } else {
//            ra.addFlashAttribute("message", "Product not found");
//            return "redirect:/admin/products";
//        }
//    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            return "admin/product-edit"; // Match your actual template name
        } else {
            ra.addFlashAttribute("message", "Product not found");
            return "redirect:/admin/products";
        }
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

    // Endpoint to serve product images
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
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
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