package com.ecommerce.inventory_service.application.services;

import com.ecommerce.inventory_service.domain.exceptions.BusinessRuleViolationException;
import com.ecommerce.inventory_service.domain.exceptions.InvalidProductOwnershipException;
import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.application.commands.CreateInventoryCommand;
import com.ecommerce.inventory_service.domain.ports.in.CreateInventoryUseCase;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import com.ecommerce.inventory_service.domain.ports.out.ProductValidationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateInventoryService implements CreateInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final ProductValidationPort productValidation;

    public CreateInventoryService(InventoryRepositoryPort inventoryRepository, ProductValidationPort productValidation) {
        this.inventoryRepository = inventoryRepository;
        this.productValidation = productValidation;
    }

    @Override
    @Transactional
    public Inventory execute(CreateInventoryCommand command, UUID sellerId) {
        // Validar que el producto exista y pertenezca al vendedor
        if(!productValidation.isProductOwnedBySeller(command.productId(), sellerId)) {
            throw new InvalidProductOwnershipException("Validación de producto fallida: El producto con ID: " + command.productId() +
                    " no existe ó el usuario no es dueño del producto");
        }
        // Validar que no se cree un inventario duplicado para el mismo Producto o SKU
        if (inventoryRepository.findByProductId(command.productId()).isPresent()) {
            throw new BusinessRuleViolationException("Ya existe un inventario registrado para el producto ID: " + command.productId());
        }
        if (inventoryRepository.findBySku(command.sku()).isPresent()) {
            throw new BusinessRuleViolationException("Ya existe un inventario registrado con el SKU: " + command.sku());
        }

        Inventory newInventory = Inventory.registerInventory(
                command.productId(),
                command.sku(),
                command.initialStock()
        );

        return inventoryRepository.save(newInventory);
    }

}
