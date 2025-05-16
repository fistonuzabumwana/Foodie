package com.example.Foodie.controller.admin;

import com.example.Foodie.model.Order;
import com.example.Foodie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    // Define possible order statuses (could also come from a config or enum)
    private final List<String> ORDER_STATUSES = Arrays.asList(
            "PENDING_PAYMENT", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED", "CONFIRMED"
    );


    @Autowired
    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listAllOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Validate page and size parameters
        page = (page < 1) ? 1 : page;
        size = (size < 1 || size > 50) ? DEFAULT_PAGE_SIZE : size;

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("orderDate").descending());
        Page<Order> orderPage = orderService.findAllOrdersWithUserAndItems(pageable);

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("statuses", ORDER_STATUSES);
//
//        List<Order> orders = orderService.findAllOrdersOrderByDateDesc();
//        model.addAttribute("orders", orders);
        return "admin/orders-list"; // Path to admin/orders-list.html
    }

    // Admin view for a specific order
    @GetMapping("/view/{orderId}")
    public String viewOrderDetails(@PathVariable("orderId") Long orderId, Model model, RedirectAttributes redirectAttributes) {
//        Optional<Order> orderOptional = orderService.findOrderById(orderId); // Using the general findOrderById
//        if (orderOptional.isPresent()) {
//            model.addAttribute("order", orderOptional.get());
//            model.addAttribute("statuses", ORDER_STATUSES); // For status update dropdown
//            return "admin/order-detail"; // Path to admin/order-detail.html
//        } else {
//            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
//            return "redirect:/admin/orders";
//        }
        Optional<Order> orderOptional = orderService.findOrderWithUserAndItems(orderId);
        if (orderOptional.isPresent()) {
            model.addAttribute("order", orderOptional.get());
            model.addAttribute("statuses", ORDER_STATUSES);
            return "admin/order-detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found.");
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/update-status/{orderId}")
    public String updateOrderStatus(@PathVariable("orderId") Long orderId,
                                    @RequestParam("status") String newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " status updated to " + newStatus);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating order status: " + e.getMessage());
        }
        return "redirect:/admin/orders/view/" + orderId; // Redirect back to the order detail view
    }
}