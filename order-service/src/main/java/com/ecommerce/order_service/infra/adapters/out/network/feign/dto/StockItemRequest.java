package com.ecommerce.order_service.infra.adapters.out.network.feign.dto;

public record StockItemRequest(
        Long productId,
        int quantity
) {
}
