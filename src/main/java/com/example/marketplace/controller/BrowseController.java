package com.example.marketplace.controller;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.Basket;
import com.example.marketplace.repoository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class BrowseController {


    private final ProductRepository productRepository;

    public BrowseController(ProductRepository productRepository){
        this.productRepository = productRepository;
    }


    @GetMapping({"/", "/home"})
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("message", "Hottest Items");
        model.addAttribute("path", request.getRequestURI());
        // make a call to repository to get 3 hottest selling items
        return "home";
    }

    @GetMapping( "/catalogue")
    public String catalogue(Model model, HttpServletRequest request) {

        List<Product> products = this.productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("message", "Catalogue");
        model.addAttribute("item_name", "Item");
        model.addAttribute("price", "€0.00");
        model.addAttribute("path", request.getRequestURI());
        return "catalogue";
    }

    @GetMapping( "/products/{id}")
    public String product(@PathVariable Long id, Model model, HttpServletRequest request) {
        Optional<Product> productOptional = this.productRepository.findById(id);
        List<Product> recommendedProducts = this.productRepository.find3RandomExcluding(id);
        Product product;
        if (productOptional.orElse(null) != null){
             product = productOptional.orElse(null);
             String condition = product.getCondition().getLabel();
             model.addAttribute("condition", condition);
             model.addAttribute("product", product);
        }
        else{
            model.addAttribute("product", null);
        }
        model.addAttribute("recommended_products", recommendedProducts);
        model.addAttribute("path", request.getRequestURI());
        return "product";
    }

    @PostMapping("/addToBasket")
    public void addToBasket(@PathVariable Long id,Model model, HttpServletRequest request){

        HttpSession session = request.getSession(false);

        Basket basket = (Basket) session.getAttribute("basket");
        Long userId = basket.getUserId();

        basket.items.put(userId, basket.items.getOrDefault(userId, 0) + 1);

    }



}