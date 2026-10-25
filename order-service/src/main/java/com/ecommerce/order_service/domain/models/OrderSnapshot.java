package com.ecommerce.order_service.domain.models;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderSnapshot(
        UUID id,
        String status,
        List<OrderItemSnapshot> items,
        BigDecimal totalAmount
) {}

