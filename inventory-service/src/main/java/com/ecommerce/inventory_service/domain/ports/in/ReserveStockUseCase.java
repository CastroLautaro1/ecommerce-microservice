package com.ecommerce.inventory_service.domain.ports.in;

import com.ecommerce.inventory_service.application.commands.ReserveStockCommand;

import java.util.UUID;

public interface ReserveStockUseCase {
    // Devolvemos el UUID que genera la reserva
    UUID execute(ReserveStockCommand command);
}
