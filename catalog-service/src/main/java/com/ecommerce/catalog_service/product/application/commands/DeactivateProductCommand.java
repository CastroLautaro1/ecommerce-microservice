package com.ecommerce.catalog_service.product.application.commands;

import java.util.UUID;

public record DeactivateProductCommand(
        Long productId,
        UUID requestingUserId
) {
}
