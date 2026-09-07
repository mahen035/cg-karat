package com.orbit.catalog.controller;

import com.orbit.catalog.entity.Product;
import com.orbit.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {

        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll(@RequestParam(name="category", required=false) String category) {
        return category != null ? productService.findByCategory(category) : productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable("id") Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }
}

// Case Study1: URL Shortner
// http://localhost:8080/api/products/2 => http://orbit.ly/2

// Generate a random shorter string for the url
