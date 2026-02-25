package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.marketplace.model.Basket;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    private Basket getBasketFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Basket) session.getAttribute("basket");
    }

    private void saveBasket(HttpServletRequest request, Basket basket) {
        HttpSession session = request.getSession(false);
        if (session != null && basket != null) {
            session.setAttribute("basket", basket);
        }
    }

    @GetMapping( "/basket")
    public String basket(Model model, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
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

    @PostMapping("/basket/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        if (basket != null) {
            basket.removeItem(id);
            saveBasket(request, basket);
        }
        return "redirect:/basket";
    }

    @PostMapping("/basket/increment/{id}")
    public String incrementQuantity(@PathVariable Long id, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        if (basket != null) {
            basket.incrementItem(id);
            saveBasket(request, basket);
        }
        return "redirect:/basket";
    }

    @PostMapping("/basket/decrement/{id}")
    public String decrementQuantity(@PathVariable Long id, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        if (basket != null) {
            basket.decrementItem(id);
            saveBasket(request, basket);
        }
        return "redirect:/basket";
    }
}
