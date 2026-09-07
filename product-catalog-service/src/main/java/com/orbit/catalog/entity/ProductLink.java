package com.orbit.catalog.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="product_links", indexes = @Index(name="idx_short_code",
                                              columnList = "shortCode", unique = true))
public class ProductLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String shortCode;
    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    private long clickCount=0; // Used for marketing purpose

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public ProductLink(){}

    public ProductLink(String shortCode, Long productId){
        this.shortCode = shortCode;
        this.productId = productId;


    }

}
