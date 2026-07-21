package com.ecommerce.inventory_service.application;

import com.ecommerce.inventory_service.domain.exceptions.ReservationNotFoundException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.in.ConfirmOrderReservationsUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ConfirmOrderReservationsService implements ConfirmOrderReservationsUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    public ConfirmOrderReservationsService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        // Busca todos los inventarios involucrados
        List<Inventory> affectedInventories = inventoryRepository.findInventoriesWithReservationsByOrderId(orderId);

        if (affectedInventories.isEmpty()) {
            // Si no hay inventarios entonces la reserva ya fue confirmada/cancelada o la orden nunca existió
            return;
        }

        // El dominio se encarfga de confirmar todas las Reservas
        for (Inventory inventory : affectedInventories) {
            inventory.confirmReservationsForOrder(orderId);
        }

        inventoryRepository.saveAll(affectedInventories);
    }
}
