package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrdersController {

    @GetMapping( "/orders")
    public String orders(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURI());
        return "orders";
    }
}
