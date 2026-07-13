package com.ecommerce.inventory_service.infra.adapter.in.web.dto;

public record CreateInventoryRequest(
        Long productId,
        String sku,
        int initialStock
) {
}
