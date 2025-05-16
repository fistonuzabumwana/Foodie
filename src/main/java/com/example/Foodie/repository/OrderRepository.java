package com.example.Foodie.repository;

import com.example.Foodie.model.Order;
import com.example.Foodie.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    // New method for admin to get all orders, sorted
    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItemsAndProductsOrderByOrderDateDesc(User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.product WHERE o.id = :orderId")
    Optional<Order> findByIdWithItemsAndProducts(Long orderId);

    // Fixed entity graphs - removed non-existent "profile" and corrected "items" to "orderItems"
    @EntityGraph(attributePaths = {"user", "user.roles", "orderItems", "orderItems.product"})
    @Query("SELECT o FROM Order o")
    Page<Order> findAllWithUserAndItems(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "user.roles", "orderItems", "orderItems.product"})
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    Optional<Order> findWithUserAndItems(@Param("orderId") Long orderId);


    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.user u " +
            "LEFT JOIN FETCH u.userProfile " +  // Changed from profile to userProfile
            "LEFT JOIN FETCH o.orderItems i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.id = :orderId")
    Optional<Order> findByIdWithUserAndItemsAndProfile(@Param("orderId") Long orderId);
}