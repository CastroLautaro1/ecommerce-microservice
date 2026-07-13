package com.ecommerce.inventory_service.domain.ports.out;

import com.ecommerce.inventory_service.domain.models.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long id);

    // Vital para cuando el Order-Service mande el pedido con los IDs del producto
    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findBySku(String sku);

    // Método ultra útil para buscar qué producto contiene una reserva específica al confirmar o cancelar
    Optional<Inventory> findByReservationId(UUID reservationId);

    // Busqueda con bloqueo pesimista para operaciones transaccionales crriticas
    Optional<Inventory> findByProductIdWithLock(Long productId);
}
