package com.example.Foodie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Product name cannot be empty")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;

    @Column(columnDefinition = "TEXT") // <<-- CHANGE HERE
    private String description;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

//    private String imageUrl;

    @NotNull(message = "Stock quantity cannot be null")
    private int stockQuantity;

    @Column(name = "image_file_id")
    private String imageFileId; // Reference to MongoDB GridFS file

    // In com.example.Foodie.model.Product.java
// ... other fields ...
    @Size(max = 100) // Example validation
    private String category;
// Add getter
// or getActive() if using Boolean
    // ... ensure getters/setters are generated (e.g., by Lombok's @Data) ...
@Column(name = "active")  // Explicit column mapping
private boolean active; // or Boolean if it can be null

    // Add this method to get the image URL
    public String getImageUrl() {
        return this.imageFileId != null ? "/products/image/" + this.id : "/images/default-food.png";
    }


}