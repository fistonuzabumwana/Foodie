package com.example.Foodie.controller;

import com.example.Foodie.model.Order;
import com.example.Foodie.model.User;
import com.example.Foodie.service.OrderService;
import com.example.Foodie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // Order Confirmation Page (also serves as order detail view for customer)
    @GetMapping("/confirmation/{orderId}")
    public String orderConfirmation(@PathVariable("orderId") Long orderId,
                                    Model model,
                                    @AuthenticationPrincipal UserDetails currentUserDetails,
                                    RedirectAttributes redirectAttributes) {
        if (currentUserDetails == null) {
            return "redirect:/login"; // Should be handled by security config
        }
        User user = userService.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        Optional<Order> orderOptional = orderService.findOrderByIdAndUser(orderId, user);

        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
            // Check for success message from redirect (e.g., after placing order)
            if (model.containsAttribute("successMessage")) {
                model.addAttribute("successMessage", model.getAttribute("successMessage"));
            }
            return "order-confirmation"; // Path to order-confirmation.html
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found or you do not have permission to view it.");
            return "redirect:/orders/history"; // Or to home page
        }
    }

    // Customer Order History
    @GetMapping("/history")
    public String orderHistory(Model model, @AuthenticationPrincipal UserDetails currentUserDetails) {
        if (currentUserDetails == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));

        List<Order> orders = orderService.findOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "order-history"; // Path to order-history.html
    }
}