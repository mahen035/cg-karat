package com.orbit.catalog.service;

import com.orbit.catalog.entity.Product;
import com.orbit.catalog.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("findAll() returns all the products from the repository")
    void findAllReturnsAllProducts(){
        //Product(String name, String description, BigDecimal price, String category, Integer stockQuantity) {
        List<Product> mockProducts = List.of(
                new Product("Mouse","wireless mouse",new BigDecimal(9.99),"Electronics",10),
                new Product("Keyboard","wireless Keyboard",new BigDecimal(29.99),"Electronics",10),
                new Product("Monitor","monitor",new BigDecimal(49.99),"Electronics",10)

        );
        when(productRepository.findAll()).thenReturn(mockProducts);
        List<Product> result = productService.findAll();

        assertEquals(3, result.size());
        assertEquals("Mouse",result.get(0).getName());
        assertEquals("Monitor", result.get(result.size()-1).getName());
    }

}
