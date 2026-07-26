package com.ecommerce.order_service.domain.event;

import java.util.UUID;

public record OrderConfirmedEvent(UUID orderId) implements DomainEvent {
}
