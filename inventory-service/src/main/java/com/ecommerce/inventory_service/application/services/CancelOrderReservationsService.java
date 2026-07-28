package com.ecommerce.inventory_service.application.services;

import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.in.CancelOrderReservationsUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CancelOrderReservationsService implements CancelOrderReservationsUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    public CancelOrderReservationsService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        // Traer todos los inventarios involucrados
        List<Inventory> affectedInventories = inventoryRepository.findInventoriesWithReservationsByOrderId(orderId);

        if (affectedInventories.isEmpty()) {
            // Si el Order-Service falla por timeout intentará compensar. Si el inventario nunca llegó a crear la reserva o ya la expiró
            // asíncronamente (Job), devolvemos éxito silencioso
            return;
        }

        // El dominio se encarga de cancelar todas las reservas
        for (Inventory inventory : affectedInventories) {
            inventory.cancelReservationsForOrder(orderId);
        }

        inventoryRepository.saveAll(affectedInventories);
    }
}
