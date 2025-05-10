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

    private String imageUrl;

    @NotNull(message = "Stock quantity cannot be null")
    private int stockQuantity;
}