package com.ecommerce.catalog_service.product.infra.adapters.out.network.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SellerStatusResponse(
        @JsonProperty("userId") Long sellerId,
        @JsonProperty("isActive") boolean isActiveSeller
) {
}
