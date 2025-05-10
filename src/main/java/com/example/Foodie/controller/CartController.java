package com.example.Foodie.controller;

import com.example.Foodie.dto.ShoppingCart;
import com.example.Foodie.model.Product;
import com.example.Foodie.service.CartService;
import com.example.Foodie.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @GetMapping
    public String viewCart(Model model) {
        ShoppingCart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cart"; // Path to cart.html
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable("productId") Long productId,
                            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isPresent()) {
            if (productOptional.get().getStockQuantity() >= quantity) {
                cartService.addProductToCart(productOptional.get(), quantity);
                redirectAttributes.addFlashAttribute("successMessage", productOptional.get().getName() + " added to cart!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock for " + productOptional.get().getName());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Product not found!");
        }
        // Redirect back to the products page or the previous page
        // For simplicity, redirecting to products list. You might want to redirect to the referring page.
        return "redirect:/products";
    }

    @PostMapping("/update/{productId}")
    public String updateCartItem(@PathVariable("productId") Long productId,
                                 @RequestParam("quantity") int quantity,
                                 RedirectAttributes redirectAttributes) {
        if (quantity < 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Quantity cannot be negative.");
            return "redirect:/cart";
        }
        Optional<Product> productOptional = productService.getProductById(productId);
        if(productOptional.isPresent() && productOptional.get().getStockQuantity() < quantity && quantity > 0){
            redirectAttributes.addFlashAttribute("errorMessage", "Not enough stock for " + productOptional.get().getName() + ". Max available: " + productOptional.get().getStockQuantity());
            return "redirect:/cart";
        }

        cartService.updateProductQuantityInCart(productId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully!");
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable("productId") Long productId, RedirectAttributes redirectAttributes) {
        cartService.removeProductFromCart(productId);
        redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart.");
        return "redirect:/cart";
    }
}