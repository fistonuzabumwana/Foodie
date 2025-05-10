package com.example.Foodie.service;

import com.example.Foodie.dto.CartItem;
import com.example.Foodie.dto.ShoppingCart;
import com.example.Foodie.model.Product;
import jakarta.servlet.http.HttpSession; // For Spring Boot 3.x
// import javax.servlet.http.HttpSession; // For older Spring Boot versions
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope; // Or manage manually

@Service
// @SessionScope // One way to manage session-scoped beans.
// Alternatively, inject HttpSession directly and manage attribute manually.
// Let's go with direct HttpSession injection for clarity here.
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "shoppingCart";
    private final HttpSession httpSession;
    private final ProductService productService; // To fetch product details

    @Autowired
    public CartServiceImpl(HttpSession httpSession, ProductService productService) {
        this.httpSession = httpSession;
        this.productService = productService;
    }

    @Override
    public ShoppingCart getCart() {
        ShoppingCart cart = (ShoppingCart) httpSession.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ShoppingCart();
            httpSession.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public void addProductToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return; // Or throw an exception
        }
        ShoppingCart cart = getCart();
        CartItem cartItem = new CartItem(product, quantity);
        cart.addItem(cartItem);
        httpSession.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void updateProductQuantityInCart(Long productId, int quantity) {
        ShoppingCart cart = getCart();
        cart.updateItemQuantity(productId, quantity);
        httpSession.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void removeProductFromCart(Long productId) {
        ShoppingCart cart = getCart();
        cart.removeItem(productId);
        httpSession.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void clearCart() {
        ShoppingCart cart = getCart();
        cart.clear();
        httpSession.setAttribute(CART_SESSION_KEY, cart);
    }
}