package com.ecommerce.inventory_service.infra.adapter.out.network.feign.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OwnershipValidationResponse(
        @JsonProperty("isOwnedAndActive") boolean isValid
) {
}
