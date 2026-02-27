package com.example.marketplace.service;

import com.example.marketplace.model.Order;
import com.example.marketplace.model.OrderStatus;
import com.example.marketplace.repoository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;

    public AdminOrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAllWithItems();
    }

    public Optional<Order> updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return Optional.empty();
        }

        Order order = optionalOrder.get();
        order.setOrderStatus(orderStatus);
        return Optional.of(orderRepository.save(order));
    }
}
