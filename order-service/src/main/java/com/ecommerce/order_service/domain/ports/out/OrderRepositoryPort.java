package com.ecommerce.order_service.domain.ports.out;

import com.ecommerce.order_service.domain.models.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderId(UUID orderId);
}
