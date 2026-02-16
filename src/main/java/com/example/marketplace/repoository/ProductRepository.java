package com.example.marketplace.repoository;

import com.example.marketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM PRODUCT WHERE ID <> :id ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Product> find3RandomExcluding(@Param("id") Long id);
}
