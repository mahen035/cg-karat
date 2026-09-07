package com.orbit.inventory.controller;

import com.orbit.inventory.entity.InventoryItem;
import com.orbit.inventory.repository.InventoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository inventoryRepository;

    public InventoryController(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryItem> get(@PathVariable("productId") Long productId) {
        return inventoryRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(new InventoryItem(productId, 100))); // default stock for demo
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<?> reserve(@PathVariable("productId") Long productId, @RequestBody Map<String, Integer> body) {
        int qty = body.getOrDefault("quantity", 1);
        InventoryItem item = inventoryRepository.findById(productId)
                .orElse(new InventoryItem(productId, 100));

        if (item.getAvailableQuantity() < qty) {
            return ResponseEntity.badRequest().body(Map.of("error", "Insufficient stock"));
        }
        item.setAvailableQuantity(item.getAvailableQuantity() - qty);
        item.setReservedQuantity(item.getReservedQuantity() + qty);
        inventoryRepository.save(item);
        return ResponseEntity.ok(item);
    }
}
