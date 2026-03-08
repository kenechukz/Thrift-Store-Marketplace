package com.example.marketplace.repository;

import com.example.marketplace.model.OrderItem;
import com.example.marketplace.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT oi.product
            FROM OrderItem oi
            WHERE oi.product.visible = true OR oi.product.visible IS NULL
            GROUP BY oi.product
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<Product> findTopSellingProducts(Pageable pageable);

    @Modifying
    @Query("UPDATE OrderItem oi SET oi.product = NULL WHERE oi.product.id = :productId")
    void clearProductReference(@Param("productId") Long productId);
}
