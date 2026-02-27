package com.example.marketplace.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.example.marketplace.model.PrincipalType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        PrincipalType principalType = session != null ? (PrincipalType) session.getAttribute("principalType") : null;
        if (principalType == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (isAdminPath(path) && principalType != PrincipalType.ADMIN) {
            response.sendRedirect("/home");
            return false;
        }

        if (isUserOnlyPath(path) && principalType != PrincipalType.USER) {
            response.sendRedirect("/home");
            return false;
        }

        if (session != null) {
            return true;
        }
        response.sendRedirect("/home");
        return false;
    }

    private boolean isPublicPath(String path) {
        return path.equals("/login")
                || path.equals("/register")
                || path.equals("/error")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/h2-console/");
    }

    private boolean isAdminPath(String path) {
        return path.startsWith("/admin");
    }

    private boolean isUserOnlyPath(String path) {
        return path.startsWith("/basket")
                || path.startsWith("/checkout")
                || path.equals("/orders")
                || path.startsWith("/orders/");
    }
}
