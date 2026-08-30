package com.ecommerce.catalog_service.product.infra.adapters.out.network.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record SellerStatusResponse(
        @JsonProperty("userId") UUID sellerId,
        @JsonProperty("isActive") boolean isActiveSeller
) {
}
