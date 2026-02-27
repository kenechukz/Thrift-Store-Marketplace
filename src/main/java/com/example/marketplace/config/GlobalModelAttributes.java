package com.example.marketplace.config;

import com.example.marketplace.model.Basket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("basketNavCount")
    public int basketNavCount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return 0;
        }

        Basket basket = (Basket) session.getAttribute("basket");
        if (basket == null) {
            return 0;
        }

        return basket.getTotalQuantity();
    }
}
