package com.orbit.catalog.repository;

import com.orbit.catalog.entity.ProductLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLinkRepository extends JpaRepository<ProductLink,Long> {
    Optional<ProductLink> findByShortCode(String shortCode);
    Optional<ProductLink> findByProductId(Long productId);
}
