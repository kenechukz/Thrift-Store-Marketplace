package com.example.marketplace.controller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

@Controller
public class ShopController {


    @GetMapping({"/", "/home"})
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("message", "Hottest Items");
        model.addAttribute("path", request.getRequestURI());
        return "home";
    }

    @GetMapping( "/catalogue")
    public String catalogue(Model model, HttpServletRequest request) {
        model.addAttribute("message", "Catalogue");
        model.addAttribute("item_name", "Item");
        model.addAttribute("price", "€0.00");
        model.addAttribute("path", request.getRequestURI());
        return "catalogue";
    }

    @GetMapping( "/basket")
    public String basket(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURI());
        return "basket";
    }

    @GetMapping( "/orders")
    public String orders(Model model, HttpServletRequest request) {
        model.addAttribute("path", request.getRequestURI());
        return "orders";
    }
}