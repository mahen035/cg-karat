package com.orbit.catalog;

import com.orbit.catalog.entity.Product;
import com.orbit.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(classes = CatalogApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductCatalogIntegrationTest {

    static{
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16")
            .withDatabaseName("demodb")
            .withUsername("postgres")
            .withPassword("demo123");


    @DynamicPropertySource
   static void configureDatabase(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

    }
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleadDb(){

        productRepository.deleteAll();
    }

    private String url(String path){
        return "http://localhost:"+port + path;
    }

    @Test
    void shouldCreateProduct(){
        Product product = new Product("iPhone 17", "Apple", new BigDecimal(999.00),"Electronics", 10);
        ResponseEntity<Product> response = restTemplate.postForEntity(url("/api/products"), product, Product.class);
        assertEquals("200 OK", response.getStatusCode().toString());
    }

}
