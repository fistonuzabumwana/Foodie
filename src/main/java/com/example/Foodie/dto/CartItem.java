package com.example.Foodie.dto; // Or your chosen package for cart-related DTOs

import com.example.Foodie.model.Product; // Assuming you want to store the whole product or just relevant fields
import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    // Getters and Setters (Lombok @Data is also great here)
    @Setter
    private Long productId;
    @Setter
    private String productName;
    @Setter
    private BigDecimal price;
    @Setter
    private String imageUrl;
    private int quantity;
    private BigDecimal subtotal;

    public CartItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.quantity = quantity;
        this.subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.price != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // You might want to update subtotal when quantity or price changes
    public void recalculateSubtotal() {
        if (this.price != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}