package com.ecommerce.inventory_service.application;

import com.ecommerce.inventory_service.domain.exceptions.ReservationNotFoundException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.in.CancelReservationUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelReservationService implements CancelReservationUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    public CancelReservationService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void execute(UUID reservationId) {
        Inventory inventory = inventoryRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        // El dominio pasa la reserva a CANCELLED y le suma las unidades de vuelta al availableStock
        inventory.cancelReservation(reservationId);

        inventoryRepository.save(inventory);
    }
}
