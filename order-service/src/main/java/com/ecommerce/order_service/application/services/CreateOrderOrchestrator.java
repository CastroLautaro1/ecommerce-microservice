package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.application.commands.CreateOrderCommand;
import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.CatalogClientPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateOrderOrchestrator implements CreateOrderUseCase {

    private final CatalogClientPort catalogClient;
    private final CreateOrderService createOrderService;

    public CreateOrderOrchestrator(CatalogClientPort catalogClient, CreateOrderService createOrderService) {
        this.catalogClient = catalogClient;
        this.createOrderService = createOrderService;
    }

    // Al no usar Transactional la red es libre
    @Override
    public Order execute(CreateOrderCommand command) {

        // Traemos la informacion de todos los Productos
        List<OrderItem> hydratedItems = command.items().stream().map(itemCommand -> {
            CatalogClientPort.ProductSnapshot snapshot = catalogClient.getProductSnapshot(itemCommand.productId())
                    .orElseThrow(() -> new DomainValidationException("Producto no encontrado ID: " + itemCommand.productId()));

            return new OrderItem(
                    snapshot.productId(),
                    snapshot.name(),
                    snapshot.currentPrice(),
                    itemCommand.quantity()
            );
        }).toList();

        // Se llama al Servicio quien dentro de una transaccion crea la Orden
        return createOrderService.persistAndPublish(command.orderId(), command.userId(), hydratedItems);
    }
}
