package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.domain.event.OrderPendingEvent;
import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.ports.out.EventPublisherPort;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CreateOrderService {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    public CreateOrderService(OrderRepositoryPort orderRepository, EventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    // Se invoca desde el orquestador
    @Transactional
    public Order persistAndPublish(UUID orderId, Long userId, List<OrderItem> hydratedItems) {
        if (orderRepository.findByOrderId(orderId).isPresent()) {
            throw new DomainValidationException("La orden con UUID " + orderId + " ya fue procesada.");
        }

        Order order = Order.registerOrder(orderId, userId, hydratedItems);
        orderRepository.save(order);

        eventPublisher.publish(new OrderPendingEvent(order.getOrderId(), order.getItems()));

        return order;
    }


//    @Override
//    @Transactional
//    public Order execute(CreateOrderCommand command) {
//        // Validar idempotencia para no crear ordenes duplicadas
//        if (orderRepository.findByOrderId(command.orderId()).isPresent()) {
//            throw new DomainValidationException("La orden con UUID " + command.orderId() + " ya fue procesada.");
//        }
//
//        // Se trae la informacion necesaria de los productos
//        List<OrderItem> items = new ArrayList<>();
//        for (OrderItemCommand itemCommand : command.items()) {
//            CatalogClientPort.ProductSnapshot snapshot = catalogClient.getProductSnapshot(itemCommand.productId())
//                    .orElseThrow(() -> new DomainValidationException("Producto no encontrado en el catálogo ID: " + itemCommand.productId()));
//
//            // Instancia del value object para guardar los datos exactos del producto en ese momento
//            items.add(new OrderItem(
//                    snapshot.productId(),
//                    snapshot.name(),
//                    snapshot.currentPrice(),
//                    itemCommand.quantity()
//            ));
//        }
//
//        // Instanciamos una Orden la cual valida reglas de negocio internamente
//        Order order = new Order(command.orderId(), command.userId(), items);
//
//        // Persistencia Local
//        orderRepository.save(order);
//
//        // Emisión del Evento de Dominio
//        eventPublisher.publish(new OrderPendingEvent(order.getOrderId(), order.getItems()));
//
//        return order;
//    }
}
