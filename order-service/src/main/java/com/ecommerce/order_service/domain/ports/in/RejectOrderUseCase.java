package com.ecommerce.order_service.domain.ports.in;

import java.util.UUID;

public interface RejectOrderUseCase {
    void execute(UUID orderId);
}
