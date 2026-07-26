package com.ecommerce.order_service.domain.ports.out;

import com.ecommerce.order_service.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
