package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.example.marketplace.model.Order;
import com.example.marketplace.model.PrincipalType;
import com.example.marketplace.repoository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class OrdersController {
    private final OrderRepository orderRepository;

    public OrdersController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping( "/orders")
    public String orders(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        PrincipalType principalType = session != null ? (PrincipalType) session.getAttribute("principalType") : null;
        if (principalType != PrincipalType.USER) {
            return "redirect:/home";
        }

        Long userId = session != null ? (Long) session.getAttribute("userId") : null;
        List<Order> orders = userId != null ? orderRepository.findByUserIdWithItems(userId) : List.of();

        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("orders", orders);
        return "orders";
    }
}
