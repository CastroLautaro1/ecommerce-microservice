package com.ecommerce.order_service.domain.event;

import java.util.UUID;

public record OrderCancelledEvent(UUID orderId) implements DomainEvent {
}
