package com.ecommerce.catalog_service.domain.ports.in;

public record DeactivateProductCommand(
        Long productId,
        Long requestingUserId
) {
}
