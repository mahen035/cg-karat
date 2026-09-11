package com.orbit.order.service;

import com.orbit.order.client.InventoryClient;
import com.orbit.order.client.PaymentClient;
import com.orbit.order.client.ProductCatalogClient;
import com.orbit.order.dto.ProductResponse;
import com.orbit.order.entity.Order;
import com.orbit.order.event.OrderEvent;
import com.orbit.order.event.OrderEventPublisher;
import com.orbit.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Orchestrates the checkout flow:
 *  1. Reserve inventory
 *  2. Charge payment (circuit-breaker protected)
 *  3. Persist the order
 *  4. Publish an OrderEvent asynchronously (never blocks on step 4)
 *
 * This is the orchestration logic behind Day 24, Case Study 3's end-to-end
 * architecture diagram.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    private final OrderEventPublisher eventPublisher;
    private final ProductCatalogClient productCatalogClient;

    public OrderService(OrderRepository orderRepository, PaymentClient paymentClient,
                         InventoryClient inventoryClient, OrderEventPublisher eventPublisher,
                        ProductCatalogClient productCatalogClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
        this.eventPublisher = eventPublisher;
        this.productCatalogClient = productCatalogClient;
    }

    public Order checkout(Long productId, int quantity) {

        ProductResponse product = productCatalogClient.getProductById(productId);

        System.out.println("::::::Product service successfully called:::::::::");

        BigDecimal unitPrice = product.getPrice();

        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        boolean reserved = inventoryClient.reserveStock(productId, quantity);
        if (!reserved) {
            Order failed = new Order(productId, quantity, total, "CANCELLED");
            return orderRepository.save(failed);
        }

        Order order = new Order(productId, quantity, total, "PENDING");
        order = orderRepository.save(order);

        Map<String, Object> paymentResult = paymentClient.charge(order.getId(), total);
        String paymentStatus = String.valueOf(paymentResult.getOrDefault("status", "FAILED"));

        if ("SUCCESS".equals(paymentStatus)) {
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
            eventPublisher.publish(OrderEvent.orderConfirmed(order.getId(), productId));
        } else {
            order.setStatus("PAYMENT_FAILED");
            orderRepository.save(order);
            eventPublisher.publish(OrderEvent.paymentFailed(order.getId(), productId));
        }

        return order;
    }
}
