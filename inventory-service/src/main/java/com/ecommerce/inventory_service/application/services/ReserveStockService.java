package com.ecommerce.inventory_service.application.services;

import com.ecommerce.inventory_service.domain.exceptions.InventoryNotFoundException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.application.commands.ItemReservationCommand;
import com.ecommerce.inventory_service.application.commands.ReserveStockCommand;
import com.ecommerce.inventory_service.domain.ports.in.ReserveStockUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        //  Ordenamos los items por productId de menor a mayor para prevenir un Deadlock
        List<ItemReservationCommand> sortedItems = command.items().stream()
                .sorted(Comparator.comparing(ItemReservationCommand::productId))
                .toList();

        // Los productos reservados van a compartir un mismo orderId
        UUID globalOrderId = command.orderId();
        Instant expiresAt = Instant.now().plus(RESERVATION_EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        // Unit work en memoria que recopila los inventarios a los que accedemos, para luego actualizarlos todos juntos
        List<Inventory> mutatedInventories = new ArrayList<>();

        for (ItemReservationCommand item : sortedItems) {
            Inventory inventory = inventoryRepository.findByProductIdWithLock(item.productId())
                    .orElseThrow(() -> new InventoryNotFoundException(item.productId()));

            UUID itemReservationId = UUID.randomUUID();

            // Creamos la reserva correspondiente para cada agregado
            inventory.reserve(
                    itemReservationId,
                    globalOrderId,
                    item.quantity(),
                    expiresAt
            );

            // Agregamos el agregado modificado a nuestra lista
            mutatedInventories.add(inventory);
        }

        // Guardamos todo de una vez al final del flujo, asi se optimiza el batching de SQL
        inventoryRepository.saveAll(mutatedInventories);

        return globalOrderId;
    }

}
