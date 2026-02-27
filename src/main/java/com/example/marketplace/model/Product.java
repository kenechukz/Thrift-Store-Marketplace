package com.example.marketplace.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String material;
    @Enumerated(EnumType.STRING)@Column(nullable = false)
    private Size size;
    @Enumerated(EnumType.STRING)@Column(nullable = false)
    private ProductCondition condition;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false, length = 300)
    private String description;
    @Column(nullable = false, length = 300)
    private String imageUrl;
    @Column
    private Boolean visible = true;


    public Product(){

    }

    public Product(String title, String material, Size size, ProductCondition condition, BigDecimal price, String description, String imageUrl){
        this.title = title;
        this.material = material;
        this.size = size;
        this.condition = condition;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getMaterial() {
        return this.material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }

    public Size getSize() {
        return this.size;
    }
    public void setSize(Size size) {
        this.size = size;
    }

    public ProductCondition getCondition() {
        return this.condition;
    }
    public void setCondition(ProductCondition condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String url){
        this.imageUrl = url;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public boolean isVisible() {
        return visible == null || visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

