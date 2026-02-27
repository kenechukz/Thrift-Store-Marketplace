package com.example.marketplace.repoository;
import com.example.marketplace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
