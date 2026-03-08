package com.example.marketplace.service;

import com.example.marketplace.model.Product;
import com.example.marketplace.repository.OrderItemRepository;
import com.example.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public AdminProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        product.setVisible(true);
        return productRepository.save(product);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product existingProduct = optionalProduct.get();
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setMaterial(updatedProduct.getMaterial());
        existingProduct.setSize(updatedProduct.getSize());
        existingProduct.setCondition(updatedProduct.getCondition());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());

        return Optional.of(productRepository.save(existingProduct));
    }

    public Optional<Product> updateVisibility(Long id, boolean visible) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return Optional.empty();
        }

        Product existingProduct = optionalProduct.get();
        existingProduct.setVisible(visible);
        return Optional.of(productRepository.save(existingProduct));
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return false;
        }

        orderItemRepository.clearProductReference(id);
        productRepository.deleteById(id);
        return true;
    }
}
