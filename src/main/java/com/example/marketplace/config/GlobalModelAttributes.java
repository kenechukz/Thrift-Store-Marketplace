package com.example.marketplace.config;

import com.example.marketplace.model.Basket;
import com.example.marketplace.model.PrincipalType;
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
        PrincipalType principalType = (PrincipalType) session.getAttribute("principalType");
        if (basket == null || principalType != PrincipalType.USER) {
            return 0;
        }

        return basket.getTotalQuantity();
    }

    @ModelAttribute("principalType")
    public PrincipalType principalType(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (PrincipalType) session.getAttribute("principalType");
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(HttpServletRequest request) {
        PrincipalType principalType = principalType(request);
        return principalType == PrincipalType.ADMIN;
    }

    @ModelAttribute("isUser")
    public boolean isUser(HttpServletRequest request) {
        PrincipalType principalType = principalType(request);
        return principalType == PrincipalType.USER;
    }
}
