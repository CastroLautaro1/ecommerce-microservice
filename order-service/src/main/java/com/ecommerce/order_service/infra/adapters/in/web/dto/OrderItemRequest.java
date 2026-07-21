package com.ecommerce.order_service.infra.adapters.in.web.dto;

public record OrderItemRequest(
        Long productId,
        int quantity
) {}
