package com.example.marketplace.repoository;

import com.example.marketplace.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM PRODUCT WHERE (visible = true OR visible IS NULL) AND ID <> :id ORDER BY RAND() LIMIT 3", nativeQuery = true)
    List<Product> find3RandomExcluding(@Param("id") Long id);

    @Query(value = "SELECT * FROM PRODUCT WHERE visible = true OR visible IS NULL", nativeQuery = true)
    List<Product> findByVisibleTrue();

    @Query(value = "SELECT * FROM PRODUCT WHERE (visible = true OR visible IS NULL) ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Product> findVisibleRandom(@Param("limit") int limit);

    Optional<Product> findByImageUrl(String imageUrl);
}
