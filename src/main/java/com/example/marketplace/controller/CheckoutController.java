package com.example.marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.marketplace.model.Basket;
import com.example.marketplace.model.Order;
import com.example.marketplace.model.OrderItem;
import com.example.marketplace.model.OrderStatus;
import com.example.marketplace.model.PrincipalType;
import com.example.marketplace.model.Product;
import com.example.marketplace.model.User;
import com.example.marketplace.repository.OrderRepository;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {
    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);
    private static final BigDecimal ZERO_MONEY = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CheckoutController(UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

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

    private String moneyString(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private Map<String, Object> basketMutationPayload(Basket basket, Long productId) {
        BigDecimal subTotal;
        BigDecimal shipping;
        BigDecimal total;
        Basket.BasketItem item = basket != null ? basket.items.get(productId) : null;
        int basketCount = basket != null ? basket.getTotalQuantity() : 0;
        if (basket != null){
            subTotal = basketCount > 0 ? basket.getSubtotal() : ZERO_MONEY;
            shipping = basketCount > 0 ? basket.getShipping() : ZERO_MONEY;
            total = basketCount > 0 ? basket.getTotal() : ZERO_MONEY; 
        }
        else{
            subTotal = ZERO_MONEY;
            shipping =  ZERO_MONEY;
            total =  ZERO_MONEY;
        }


        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("productId", productId);
        payload.put("removed", item == null);
        payload.put("quantity", item != null ? item.getQuantity() : 0);
        payload.put("itemSubtotal", item != null ? moneyString(item.getSubtotal()) : moneyString(ZERO_MONEY));
        payload.put("basketCount", basketCount);
        payload.put("subTotal", moneyString(subTotal));
        payload.put("shipping", moneyString(shipping));
        payload.put("total", moneyString(total));
        return payload;
    }

    private String nextOrderNumber(int value) {
        return String.format("%04d", value);
    }

    private Map<String, Object> createOrderPayload(Order order){
        List<Map<String, Object>> items = order.getOrderItems().stream()
                .map(item -> {
                    Map<String, Object> itemPayload = new LinkedHashMap<>();
                    BigDecimal purchasePrice = item.getUnitPriceAtPurchase() != null ? item.getUnitPriceAtPurchase() : item.getUnitPrice();
                    itemPayload.put("productId", item.getProduct().getId());
                    itemPayload.put("quantity", item.getQuantity());
                    itemPayload.put("unitPriceAtPurchase", moneyString(purchasePrice));
                    return itemPayload;
                })
                .toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", order.getId());
        payload.put("orderNumber", order.getOrderNumber());
        payload.put("date",order.getDate());
        payload.put("orderItems", items);
        payload.put("total", moneyString(order.getTotal()));
        payload.put("orderStatus", order.getOrderStatus());

        return payload;
    }

    @GetMapping( "/basket")
    public String basket(Model model, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        List<Basket.BasketItem> basketItems =
                basket != null ? new ArrayList<>(basket.items.values()) : List.of();
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("basketItems", basketItems);
        model.addAttribute("basketCount", basket != null ? basket.getTotalQuantity() : 0);
        model.addAttribute("subTotal", basket != null ? basket.getSubtotal() : java.math.BigDecimal.ZERO);
        model.addAttribute("shipping", basket != null ? basket.getShipping() : java.math.BigDecimal.ZERO);
        model.addAttribute("total", basket != null ? basket.getTotal() : java.math.BigDecimal.ZERO);
        return "basket";
    }

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "redirect:/basket";
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

    @PostMapping("/basket/api/remove/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCartAjax(@PathVariable Long id, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        if (basket != null) {
            basket.removeItem(id);
            saveBasket(request, basket);
        }
        return ResponseEntity.ok(basketMutationPayload(basket, id));
    }

    @PostMapping("/basket/api/increment/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> incrementQuantityAjax(@PathVariable Long id, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        if (basket != null) {
            basket.incrementItem(id);
            saveBasket(request, basket);
        }
        return ResponseEntity.ok(basketMutationPayload(basket, id));
    }

    @PostMapping("/basket/api/decrement/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> decrementQuantityAjax(@PathVariable Long id, HttpServletRequest request) {
        Basket basket = getBasketFromSession(request);
        if (basket != null) {
            basket.decrementItem(id);
            saveBasket(request, basket);
        }
        return ResponseEntity.ok(basketMutationPayload(basket, id));
    }

    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<?> checkout(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        PrincipalType principalType = session != null ? (PrincipalType) session.getAttribute("principalType") : null;
        if (principalType != PrincipalType.USER) {
            return ResponseEntity.status(403).body(Map.of("error", "Only users can checkout"));
        }

        Basket basket = getBasketFromSession(request);
        if (basket == null || basket.items.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Basket is empty"));
        }
        if (basket.getUserId() == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        Optional<User> userOptional = this.userRepository.findById(basket.getUserId());
        if (userOptional.isEmpty()){
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        try {
            User user = userOptional.get();
            BigDecimal orderTotal = BigDecimal.ZERO;
            Order order = new Order(user, LocalDateTime.now(), BigDecimal.ZERO, OrderStatus.PAID);
            Set<Long> productIds = new HashSet<>(basket.items.keySet());
            List<Product> products = productRepository.findAllById(productIds);
            Map<Long, Product> productById = products.stream()
                    .collect(Collectors.toMap(Product::getId, product -> product));

            if (products.size() != productIds.size()) {
                return ResponseEntity.badRequest().body(Map.of("error", "One or more products in basket no longer exist"));
            }

            for (Map.Entry<Long, Basket.BasketItem> entry : basket.items.entrySet()) {
                Long productId = entry.getKey();
                Basket.BasketItem basketItem = entry.getValue();
                Product product = productById.get(productId);
                if (product == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid basket product"));
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(basketItem.getQuantity());
                orderItem.setUnitPrice(product.getPrice());
                orderItem.setUnitPriceAtPurchase(product.getPrice());
                order.addOrderItem(orderItem);
                orderTotal = orderTotal.add(product.getPrice().multiply(BigDecimal.valueOf(basketItem.getQuantity())));
            }
            order.setTotal(orderTotal.add(basket.getShipping()));
            int nextOrderNumberValue = orderRepository.findMaxOrderNumberValue() + 1;
            boolean saved = false;
            for (int attempt = 0; attempt < 10; attempt++) {
                order.setOrderNumber(nextOrderNumber(nextOrderNumberValue + attempt));
                try {
                    this.orderRepository.saveAndFlush(order);
                    saved = true;
                    break;
                } catch (DataIntegrityViolationException e) {
                    log.warn("Order number {} already exists, retrying", order.getOrderNumber());
                }
            }
            if (!saved) {
                log.error("Checkout failed for user {} after exhausting sequential order number retries", basket.getUserId());
                return ResponseEntity.internalServerError().body(Map.of("error", "Unable to allocate order number"));
            }

            // Clear basket after successful checkout.
            saveBasket(request, new Basket(user.getId()));

            return ResponseEntity.status(201).body(createOrderPayload(order));
        } catch (Exception e) {
            log.error("Checkout failed for user {}", basket.getUserId(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Checkout failed"));
        }
    }
}
