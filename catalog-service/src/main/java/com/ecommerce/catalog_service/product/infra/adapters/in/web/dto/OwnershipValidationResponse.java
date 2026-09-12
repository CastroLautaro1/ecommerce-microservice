package com.ecommerce.catalog_service.product.infra.adapters.in.web.dto;

public record OwnershipValidationResponse(
        boolean isOwnedAndActive
) {
}
