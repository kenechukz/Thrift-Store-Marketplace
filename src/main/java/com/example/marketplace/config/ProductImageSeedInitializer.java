package com.example.marketplace.config;

import com.example.marketplace.model.Product;
import com.example.marketplace.model.ProductCondition;
import com.example.marketplace.model.Size;
import com.example.marketplace.repoository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;

@Component
public class ProductImageSeedInitializer implements CommandLineRunner {

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("25.00");
    private static final String DEFAULT_MATERIAL = "Mixed Fabric";
    private static final String DEFAULT_DESCRIPTION = "Auto-generated product from local image inventory.";

    private final ProductRepository productRepository;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public ProductImageSeedInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Resource[] imageResources = resolver.getResources("classpath:/static/images/*");
        for (Resource imageResource : imageResources) {
            String filename = imageResource.getFilename();
            if (filename == null || filename.isBlank()) {
                continue;
            }

            if (productRepository.findByImageUrl(filename).isPresent()) {
                continue;
            }

            Product product = new Product();
            product.setTitle(defaultTitleFromFilename(filename));
            product.setMaterial(DEFAULT_MATERIAL);
            product.setSize(Size.M);
            product.setCondition(ProductCondition.GOOD);
            product.setPrice(DEFAULT_PRICE);
            product.setDescription(DEFAULT_DESCRIPTION);
            product.setImageUrl(filename);
            product.setVisible(true);

            productRepository.save(product);
        }
    }

    private String defaultTitleFromFilename(String filename) {
        String withoutExtension = filename.replaceFirst("\\.[^.]+$", "");
        String normalized = withoutExtension.replace('_', ' ').replace('-', ' ').trim();
        if (normalized.isEmpty()) {
            return "Product";
        }

        String[] parts = normalized.toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder title = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                continue;
            }
            if (i > 0) {
                title.append(' ');
            }
            title.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                title.append(part.substring(1));
            }
        }
        return title.toString();
    }
}
