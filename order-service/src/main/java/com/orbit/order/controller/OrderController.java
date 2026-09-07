package com.orbit.order.controller;

import com.orbit.order.entity.Order;
import com.orbit.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order checkout(@RequestBody Map<String, Object> body) {
        Long productId = Long.valueOf(String.valueOf(body.get("productId")));
        Integer quantity = Integer.valueOf(String.valueOf(body.getOrDefault("quantity", 1)));
        BigDecimal unitPrice = new BigDecimal(String.valueOf(body.getOrDefault("unitPrice", "0")));
        return orderService.checkout(productId, quantity, unitPrice);
    }
}
