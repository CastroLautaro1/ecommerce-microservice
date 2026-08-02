package com.ecommerce.catalog_service.product.application.commands;

public record DeactivateProductCommand(
        Long productId,
        Long requestingUserId
) {
}
