package com.example.Foodie.service;

import com.example.Foodie.dto.ShoppingCart;
import com.example.Foodie.model.Product;

public interface CartService {
    ShoppingCart getCart();
    void addProductToCart(Product product, int quantity);
    void updateProductQuantityInCart(Long productId, int quantity);
    void removeProductFromCart(Long productId);
    void clearCart();
}