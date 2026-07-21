package com.ecommerce.inventory_service.infra.adapter.in.web.dto;

public record ItemReservationDto(
        Long productId,
        int quantity
) {}
