package com.ecommerce.order_service.infra.adapters.in.web.dto;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items
) {
}
