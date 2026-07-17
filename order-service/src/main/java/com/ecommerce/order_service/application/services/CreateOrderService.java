package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.application.commands.CreateOrderCommand;
import com.ecommerce.order_service.application.commands.OrderItemCommand;
import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.CatalogClientPort;
import com.ecommerce.order_service.domain.ports.out.InventoryClientPort;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final CatalogClientPort catalogClient;
    private final InventoryClientPort inventoryClient;

    public CreateOrderService(
            OrderRepositoryPort orderRepository,
            CatalogClientPort catalogClient,
            InventoryClientPort inventoryClient) {
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    @Override
    public Order execute(CreateOrderCommand command) {
        // Validar idempotencia para no crear ordenes duplicadas
        if (orderRepository.findByOrderId(command.orderId()).isPresent()) {
            throw new DomainValidationException("La orden con UUID " + command.orderId() + " ya fue procesada.");
        }

        // Se trae la informacion necesaria de los productos
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemCommand itemCommand : command.items()) {
            CatalogClientPort.ProductSnapshot snapshot = catalogClient.getProductSnapshot(itemCommand.productId())
                    .orElseThrow(() -> new DomainValidationException("Producto no encontrado en el catálogo ID: " + itemCommand.productId()));

            // Instancia del value object para guardar los datos exactos del producto en ese momento
            items.add(new OrderItem(
                    snapshot.productId(),
                    snapshot.name(),
                    snapshot.currentPrice(),
                    itemCommand.quantity()
            ));
        }

        // Instanciamos una Orden la cual valida reglas de negocio internamente
        Order order = new Order(command.orderId(), command.userId(), items);

        // Se reserva la cantidad correspondiente de cada producto
        UUID reservationId;
        try {
            reservationId = inventoryClient.reserveStock(order.getOrderId(), order.getItems());
            order.attachInventoryReservation(reservationId);
        } catch (Exception ex) {
            log.error("Fallo al reservar stock para la orden {}: {}", order.getOrderId(), ex.getMessage());
            // Si el inventario falla, abortamos todo. No hay estado local que revertir aún.
            throw new DomainValidationException("No se pudo completar la reserva de inventario: " + ex.getMessage());
        }

        // Se guarda la orden
        try {
            return orderRepository.save(order);
        } catch (Exception dbException) {
            // Si la transaccion falla entonces se debe liberar el stock reservado en el Inventory Service
            log.error("Fallo al guardar la orden local {}. Ejecutando compensación...", order.getOrderId());
            try {
                inventoryClient.cancelReservation(reservationId);
                log.info("Compensación exitosa: Reserva {} cancelada.", reservationId);
            } catch (Exception compensationEx) {
                // Si no se puede liberar ese Stock entonces se tendra que hacer de forma manual
                log.error("FALLO CRÍTICO DE COMPENSACIÓN. Reserva {} requiere cancelación manual.", reservationId, compensationEx);
            }
            throw new RuntimeException("Error interno al persistir la orden. Transacción abortada.");
        }
    }

}
