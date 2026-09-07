package com.orbit.catalog.service;

import com.orbit.catalog.entity.Product;
import com.orbit.catalog.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Cacheable here is the Redis-cache stand-in discussed in Case Study 1:
 * catalog reads are far more frequent than writes, so a read-through cache
 * belongs in front of the database. Swap the default (in-memory
 * ConcurrentMapCacheManager) for a RedisCacheManager in application.yml /
 * a CacheConfig bean to get the production behavior.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable("products")
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {

        return productRepository.findAll();
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}
