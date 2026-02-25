package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.marketplace.model.Basket;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    @GetMapping( "/basket")
    public String basket(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        Basket basket = null;
        if (session != null) {
            basket = (Basket) session.getAttribute("basket");
        }
        List<Basket.BasketItem> basketItems =
                basket != null ? new ArrayList<>(basket.items.values()) : List.of();
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("basketItems", basketItems);
        model.addAttribute("basketCount", basketItems.size());
        model.addAttribute("subTotal", basket != null ? basket.getSubtotal() : java.math.BigDecimal.ZERO);
        model.addAttribute("shipping", basket != null ? basket.getShipping() : java.math.BigDecimal.ZERO);
        model.addAttribute("total", basket != null ? basket.getTotal() : java.math.BigDecimal.ZERO);
        return "basket";
    }
}
