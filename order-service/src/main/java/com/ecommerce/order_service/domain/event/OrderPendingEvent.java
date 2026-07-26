package com.ecommerce.order_service.domain.event;

import com.ecommerce.order_service.domain.models.OrderItem;

import java.util.List;
import java.util.UUID;

public record OrderPendingEvent(
        UUID orderId,
        List<OrderItem> items
) implements DomainEvent{
}
