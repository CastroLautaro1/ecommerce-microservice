package com.ecommerce.inventory_service.application;

import com.ecommerce.inventory_service.domain.exceptions.InventoryNotFoundException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.models.Reservation;
import com.ecommerce.inventory_service.domain.ports.in.ReserveStockCommand;
import com.ecommerce.inventory_service.domain.ports.in.ReserveStockUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class ReserveStockService implements ReserveStockUseCase {

    // Tiempo de expiración de un carrito de compras
    private static final int RESERVATION_EXPIRATION_MINUTES = 15;

    private final InventoryRepositoryPort inventoryRepository;

    public ReserveStockService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public UUID execute(ReserveStockCommand command) {
        // Buscamos el inventario usando el puerto con bloqueo pesimista
        Inventory inventory = inventoryRepository.findByProductIdWithLock(command.productId())
                .orElseThrow(() -> new InventoryNotFoundException(command.productId()));

        // Se genera el identificador único de la nueva reserva y su fecha de expiración
        UUID reservationId = UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(RESERVATION_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        // El Dominio maneja la logica matematica y valida el stock disponible
        // Si no hay stock se arroja una excepcion y la transaccion hace un rollback
        Reservation reservation = inventory.reserve(
                reservationId,
                command.orderId(),
                command.quantity(),
                expiresAt
        );

        inventoryRepository.save(inventory);

        // Retorna el UUID para que el servicio de ordenes sepa con qué ID quedo bloqueado su stock
        return reservation.getReservationId();
    }
}
