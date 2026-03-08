package com.example.marketplace.repository;
import com.example.marketplace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>{
    @Query("""
            SELECT DISTINCT o
            FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.product
            WHERE o.user.id = :userId
            ORDER BY o.date DESC
            """)
    List<Order> findByUserIdWithItems(@Param("userId") Long userId);

    @Query("""
            SELECT DISTINCT o
            FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.product
            ORDER BY o.date DESC
            """)
    List<Order> findAllWithItems();
}
