package com.example.Foodie.service;

import com.example.Foodie.dto.CartItem;
import com.example.Foodie.dto.ShoppingCart;
import com.example.Foodie.exception.InsufficientStockException;
import com.example.Foodie.model.Order;
import com.example.Foodie.model.OrderItem;
import com.example.Foodie.model.Product;
import com.example.Foodie.model.User;
import com.example.Foodie.repository.OrderRepository;
import com.example.Foodie.repository.ProductRepository; // To update stock
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository; // For stock updates
    private final CartService cartService; // To clear the cart

    // New method for admin to get all orders, sorted

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional // This operation should be atomic
    public Order placeOrder(User user, ShoppingCart cart) throws InsufficientStockException {
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Shopping cart is empty.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING_PAYMENT"); // Or "PROCESSING", "CONFIRMED"
        order.setTotalAmount(cart.getTotal());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + cartItem.getProductName()));

            // Check stock before proceeding
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for " + product.getName() +
                        ". Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order); // Link back to the order
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice()); // Price at the time of purchase
            orderItems.add(orderItem);

            // Update stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product); // Save updated product stock
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order); // Cascade should save orderItems

        // Clear the shopping cart from session after successful order placement
        cartService.clearCart();

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderByIdAndUser(Long orderId, User user) {
        // Fetch order and ensure items are loaded if lazily fetched.
        // Using a custom query or join fetch might be more efficient if OrderItems are lazy.
        // For now, relying on Open Session In View or eager fetching if configured.
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent() && order.get().getUser().equals(user)) {
            // Trigger loading of order items if they are lazy fetched
            // and not automatically fetched by the findById or if OSIV is disabled.
            // This is a common way to do it if OrderItems is LAZY
            order.get().getOrderItems().size(); // or any operation that accesses the collection
            return order;
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findOrdersByUser(User user) {
        // Similarly, ensure OrderItems are fetched if needed for display list.
        // A custom query with JOIN FETCH might be optimal here for N+1 problem.
        // Example: "SELECT o FROM Order o JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.user = :user ORDER BY o.orderDate DESC"
        // For now, using the default Spring Data JPA method.
        List<Order> orders = orderRepository.findByUserWithItemsAndProductsOrderByOrderDateDesc(user);
        // Eagerly load order items for each order if they are LAZY
        orders.forEach(order -> order.getOrderItems().size());
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findOrderById(Long orderId) {
        // General purpose find by ID, could be used by admin
        // This was updated to use the repository method that fetches details
        return orderRepository.findByIdWithDetails(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAllOrdersOrderByDateDesc() {
        // Consider a more efficient query if you have many orders or need user details eagerly
        // For now, using a simple findAll and then sorting, or a derived query name.
        // A better repository method would be: orderRepository.findAllByOrderByOrderDateDesc();
        // Let's assume you add that to your repository.
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        // Eagerly load necessary associations if LAZY
        orders.forEach(order -> {
            order.getUser().getUsername(); // Example: touch user if needed in admin list
            order.getOrderItems().size();
        });
        return orders;
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) throws Exception {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found with ID: " + orderId));

        // Optional: Add validation for valid status transitions here
        // e.g., if (!isValidStatusTransition(order.getStatus(), newStatus)) { throw new IllegalArgumentException("Invalid status transition"); }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    public Page<Order> findAllOrdersWithUserAndItems(Pageable pageable) {
        return orderRepository.findAllWithUserAndItems(pageable);
    }

    @Override
    public Optional<Order> findOrderWithUserAndItems(Long orderId) {
        return orderRepository.findWithUserAndItems(orderId);
    }

}