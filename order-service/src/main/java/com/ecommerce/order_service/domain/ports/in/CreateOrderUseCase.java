package com.ecommerce.order_service.domain.ports.in;

import com.ecommerce.order_service.application.commands.CreateOrderCommand;
import com.ecommerce.order_service.domain.models.Order;

public interface CreateOrderUseCase {
    Order execute(CreateOrderCommand command);
}
