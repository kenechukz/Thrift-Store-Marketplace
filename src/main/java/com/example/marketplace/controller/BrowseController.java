package com.example.marketplace.controller;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

@Controller
public class BrowseController {


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

    @GetMapping( "/product")
    public String product(Model model, HttpServletRequest request) {
        model.addAttribute("product", "Jorts");
        model.addAttribute("path", request.getRequestURI());
        return "product";
    }



}