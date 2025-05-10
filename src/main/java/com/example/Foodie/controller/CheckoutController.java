package com.example.Foodie.controller;

import com.example.Foodie.dto.ShoppingCart;
import com.example.Foodie.exception.InsufficientStockException;
import com.example.Foodie.model.Order;
import com.example.Foodie.model.User;
import com.example.Foodie.service.CartService;
import com.example.Foodie.service.OrderService;
import com.example.Foodie.service.UserService; // To get current user
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService; // Or directly use UserRepository if preferred

    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService, UserService userService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String checkoutPage(Model model, @AuthenticationPrincipal UserDetails currentUserDetails, RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            // Should be handled by Spring Security, but as a fallback
            redirectAttributes.addFlashAttribute("errorMessage", "Please login to proceed to checkout.");
            return "redirect:/login";
        }
        ShoppingCart cart = cartService.getCart();
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", "Your cart is empty. Please add items before checking out.");
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        // Add any other necessary details for the checkout page
        return "checkout"; // Path to checkout.html
    }

    @PostMapping("/place-order")
    public String placeOrder(@AuthenticationPrincipal UserDetails currentUserDetails,
                             RedirectAttributes redirectAttributes, Model model) {
        if (currentUserDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login to place an order.");
            return "redirect:/login";
        }

        // Fetch the full User entity
        User user = userService.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found, though authenticated. This should not happen."));

        ShoppingCart cart = cartService.getCart();
        if (cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot place an order with an empty cart.");
            return "redirect:/cart";
        }

        try {
            Order placedOrder = orderService.placeOrder(user, cart);
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully! Your Order ID is: " + placedOrder.getId());
            // Redirect to an order confirmation page or order history
            return "redirect:/orders/confirmation/" + placedOrder.getId(); // Example confirmation page
        } catch (InsufficientStockException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order could not be placed: " + e.getMessage());
            return "redirect:/cart"; // Or back to checkout with the error
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Order could not be placed: " + e.getMessage());
            return "redirect:/cart";
        } catch (Exception e) { // Catch-all for other unexpected errors
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred while placing your order. Please try again.");
            // Log the exception e.printStackTrace();
            return "redirect:/checkout";
        }
    }
}