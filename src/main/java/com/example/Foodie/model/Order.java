package com.example.Foodie.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY fetching is often better for performance
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    private LocalDateTime orderDate = LocalDateTime.now(); // Default value

    @Column(nullable = false)
    private String status; // e.g., PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
}