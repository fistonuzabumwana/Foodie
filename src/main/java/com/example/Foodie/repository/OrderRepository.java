package com.example.Foodie.repository;

import com.example.Foodie.model.Order;
import com.example.Foodie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    // New method for admin to get all orders, sorted
    List<Order> findAllByOrderByOrderDateDesc();
    // You can add methods to find orders by status, date range, etc.

    // Inside OrderRepository.java
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    // Optional: More efficient query to fetch orders with items and products for a user
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItemsAndProductsOrderByOrderDateDesc(User user);

    // Optional: More efficient query to fetch a single order with items and products
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :orderId")
    Optional<Order> findByIdWithItemsAndProducts(Long orderId);
}