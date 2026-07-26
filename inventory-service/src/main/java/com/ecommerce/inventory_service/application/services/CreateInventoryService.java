package com.ecommerce.inventory_service.application.services;

import com.ecommerce.inventory_service.domain.exceptions.DomainValidationException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.application.commands.CreateInventoryCommand;
import com.ecommerce.inventory_service.domain.ports.in.CreateInventoryUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateInventoryService implements CreateInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepository;

    public CreateInventoryService(InventoryRepositoryPort inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public Inventory execute(CreateInventoryCommand command) {
        // Validar que no se cree un inventario duplicado para el mismo Producto o SKU
        if (inventoryRepository.findByProductId(command.productId()).isPresent()) {
            throw new DomainValidationException("Ya existe un inventario registrado para el producto ID: " + command.productId());
        }
        if (inventoryRepository.findBySku(command.sku()).isPresent()) {
            throw new DomainValidationException("Ya existe un inventario registrado con el SKU: " + command.sku());
        }

        // Instanciamos un Inventario
        Inventory newInventory = new Inventory(
                command.productId(),
                command.sku(),
                command.initialStock()
        );

        return inventoryRepository.save(newInventory);
    }

}
