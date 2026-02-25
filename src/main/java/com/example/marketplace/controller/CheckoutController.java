package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.marketplace.model.Basket;

@Controller
public class CheckoutController {

    @GetMapping( "/basket")
    public String basket(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        Basket basket = (Basket) session.getAttribute("basket");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("basket", basket.items);
        return "basket";
    }
}
