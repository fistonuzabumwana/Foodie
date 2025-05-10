package com.example.Foodie.dto; // Or your chosen package

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingCart {
    private List<CartItem> items;
    private BigDecimal total;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    // Getters and Setters
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    // Method to add an item
    public void addItem(CartItem item) {
        boolean found = false;
        for (CartItem existingItem : items) {
            if (Objects.equals(existingItem.getProductId(), item.getProductId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                existingItem.recalculateSubtotal();
                found = true;
                break;
            }
        }
        if (!found) {
            items.add(item);
        }
        calculateTotal();
    }

    // Method to update quantity
    public void updateItemQuantity(Long productId, int quantity) {
        for (CartItem item : items) {
            if (Objects.equals(item.getProductId(), productId)) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                    item.recalculateSubtotal();
                } else {
                    // If quantity is 0 or less, remove the item
                    items.remove(item);
                }
                break;
            }
        }
        calculateTotal();
    }

    // Method to remove an item
    public void removeItem(Long productId) {
        items.removeIf(item -> Objects.equals(item.getProductId(), productId));
        calculateTotal();
    }

    // Calculate total price of the cart
    public void calculateTotal() {
        this.total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public void clear() {
        this.items.clear();
        this.total = BigDecimal.ZERO;
    }
}