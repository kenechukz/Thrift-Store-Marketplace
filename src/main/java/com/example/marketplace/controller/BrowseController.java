package com.example.marketplace.controller;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.Basket;
import com.example.marketplace.model.PrincipalType;
import com.example.marketplace.repoository.OrderItemRepository;
import com.example.marketplace.repoository.ProductRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class BrowseController {


    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public BrowseController(ProductRepository productRepository, OrderItemRepository orderItemRepository){
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }


    @GetMapping({"/", "/home"})
    public String home(Model model, HttpServletRequest request) {
        List<Product> hottestProducts = this.orderItemRepository.findTopSellingProducts(PageRequest.of(0, 3));
        if (hottestProducts.isEmpty()) {
            hottestProducts = this.productRepository.findByVisibleTrue()
                    .stream()
                    .limit(3)
                    .toList();
        }

        model.addAttribute("message", "Hottest Items");
        model.addAttribute("products", hottestProducts);
        model.addAttribute("path", request.getRequestURI());
        return "home";
    }

    @GetMapping( "/catalogue")
    public String catalogue(Model model, HttpServletRequest request) {

        List<Product> products = this.productRepository.findByVisibleTrue();
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
        if (productOptional.isEmpty()) {
            return "redirect:/catalogue";
        }

        HttpSession session = request.getSession(false);
        PrincipalType principalType = session != null ? (PrincipalType) session.getAttribute("principalType") : null;
        Product product = productOptional.get();
        if (!product.isVisible() && principalType != PrincipalType.ADMIN) {
            return "redirect:/catalogue";
        }

        List<Product> recommendedProducts = this.productRepository.find3RandomExcluding(id);
        String condition = product.getCondition().getLabel();
        model.addAttribute("condition", condition);
        model.addAttribute("product", product);
        model.addAttribute("recommended_products", recommendedProducts);
        model.addAttribute("path", request.getRequestURI());
        return "product";
    }

    @PostMapping("/basket/{id}")
    public ResponseEntity<Map<String, Object>> addToBasket(@PathVariable Long id,Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        PrincipalType principalType = (PrincipalType) session.getAttribute("principalType");
        if (principalType != PrincipalType.USER) {
            return ResponseEntity.status(403).body(Map.of("error", "Only users can add items to basket"));
        }

        Basket basket = (Basket) session.getAttribute("basket");
        if (basket == null) {
            Long userId = (Long) session.getAttribute("userId");
            basket = new Basket(userId);
            session.setAttribute("basket", basket);
        }

        if (basket.items.containsKey(id)){
            basket.items.get(id).increment();
            session.setAttribute("basket", basket);
            return ResponseEntity.ok(Map.of("basketCount", basket.getTotalQuantity()));
        }

        Optional<Product> optProduct = this.productRepository.findById(id);

        if (optProduct.isPresent() && optProduct.get().isVisible()){
            basket.items.put(id, new Basket.BasketItem(optProduct.get(), 1));
            session.setAttribute("basket", basket);
            return ResponseEntity.ok(Map.of("basketCount", basket.getTotalQuantity()));
        }

        return ResponseEntity.notFound().build();


    }



}
