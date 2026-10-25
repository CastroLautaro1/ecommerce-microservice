package com.ecommerce.order_service.domain.models;

import java.math.BigDecimal;

public record OrderItemSnapshot(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        int quantity
){}
