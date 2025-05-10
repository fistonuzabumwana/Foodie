package com.example.Foodie.service;

import com.example.Foodie.dto.ShoppingCart;
import com.example.Foodie.exception.InsufficientStockException;
import com.example.Foodie.model.Order;
import com.example.Foodie.model.User;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    // Creates an order from the shopping cart for the given user
    Order placeOrder(User user, ShoppingCart cart) throws InsufficientStockException;
    // You'll add methods to find orders later (for customer history & admin view)
    // New methods
    Optional<Order> findOrderByIdAndUser(Long orderId, User user);
    List<Order> findOrdersByUser(User user);
    Optional<Order> findOrderById(Long orderId); // For admin or general fetching if needed

    // New methods for Admin
    List<Order> findAllOrdersOrderByDateDesc();
    Order updateOrderStatus(Long orderId, String newStatus) throws Exception; // Can throw if order not found or status invalid
}