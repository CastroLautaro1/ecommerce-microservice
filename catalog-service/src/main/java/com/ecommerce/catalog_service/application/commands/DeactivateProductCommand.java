package com.ecommerce.catalog_service.application.commands;

public record DeactivateProductCommand(
        Long productId,
        Long requestingUserId
) {
}
