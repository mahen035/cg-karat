package com.orbit.order.client;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class InventoryClient {

    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;

    public InventoryClient(RestTemplate restTemplate, Environment env) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrl = env.getProperty("orbit.inventory-service.url", "http://localhost:8084");
    }

    @SuppressWarnings("unchecked")
    public boolean reserveStock(Long productId, int quantity) {
        try {
            Map<String, Object> response = restTemplate.postForObject(
                    inventoryServiceUrl + "/api/inventory/" + productId + "/reserve",
                    Map.of("quantity", quantity), Map.class);
            return response != null && !response.containsKey("error");
        } catch (Exception e) {
            return false;
        }
    }
}
