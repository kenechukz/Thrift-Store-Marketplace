package com.example.marketplace.controller;

import com.example.marketplace.model.Order;
import com.example.marketplace.model.OrderStatus;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.ProductCondition;
import com.example.marketplace.model.Size;
import com.example.marketplace.service.AdminOrderService;
import com.example.marketplace.service.AdminProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class AdminController {

    private final AdminProductService adminProductService;
    private final AdminOrderService adminOrderService;

    public AdminController(AdminProductService adminProductService, AdminOrderService adminOrderService) {
        this.adminProductService = adminProductService;
        this.adminOrderService = adminOrderService;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURI());
        return "admin_dashboard";
    }

    @GetMapping("/admin/products")
    public String adminProducts(Model model, HttpServletRequest request) {
        List<Product> products = adminProductService.findAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("sizes", Size.values());
        model.addAttribute("conditions", ProductCondition.values());
        model.addAttribute("path", request.getRequestURI());
        return "admin_products";
    }

    @PostMapping("/admin/products")
    public String createProduct(Product product) {
        adminProductService.createProduct(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}")
    public String updateProduct(@PathVariable Long id, Product product) {
        adminProductService.updateProduct(id, product);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/visibility")
    public String updateVisibility(@PathVariable Long id, @RequestParam("visible") boolean visible) {
        adminProductService.updateVisibility(id, visible);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model, HttpServletRequest request) {
        List<Order> orders = adminOrderService.findAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("path", request.getRequestURI());
        return "admin_orders";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(@PathVariable UUID id, @RequestParam("orderStatus") OrderStatus orderStatus) {
        adminOrderService.updateOrderStatus(id, orderStatus);
        return "redirect:/admin/orders";
    }
}
