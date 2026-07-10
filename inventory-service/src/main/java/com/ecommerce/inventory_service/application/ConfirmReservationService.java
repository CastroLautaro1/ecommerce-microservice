package com.ecommerce.inventory_service.application;

import com.ecommerce.inventory_service.domain.exceptions.ReservationNotFoundException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.in.ConfirmReservationUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfirmReservationService implements ConfirmReservationUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    public ConfirmReservationService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void execute(UUID reservationId) {
        Inventory inventory = inventoryRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        // El dominio cambia el estado a CONFIRMED y descuenta el totalStock fisico del almacen
        inventory.confirmReservation(reservationId);

        inventoryRepository.save(inventory);
    }
}
