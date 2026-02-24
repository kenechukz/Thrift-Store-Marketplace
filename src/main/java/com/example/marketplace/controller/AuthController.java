package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {


    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request){

        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request){

        return "register";
    }
}
