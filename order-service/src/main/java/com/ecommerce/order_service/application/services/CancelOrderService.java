package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.domain.exceptions.OrderNotFoundException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OutboxEvent;
import com.ecommerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import com.ecommerce.order_service.domain.ports.out.OutboxRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OutboxRepositoryPort outboxRepository;
    private final ObjectMapper objectMapper;
    //private final EventPublisherPort eventPublisher;

    public CancelOrderService(OrderRepositoryPort orderRepository, OutboxRepositoryPort outboxRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        // Cancelo el estado de la Orden y la guardo
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.cancel();
        orderRepository.save(order);

        try {
            String payload = objectMapper.writeValueAsString(order.toSnapshot());

            // Se crea y guarda el OutboxEvent para luego comunicarse con el Inventario
            OutboxEvent outboxEvent = OutboxEvent.createPending(
                    order.getOrderId(),
                    "ORDER",
                    "ORDER_CANCELLED",
                    payload
            );
            outboxRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Error fatal al serializar el payload del Outbox", e);
        }

        // Llamada al listener para cancelar las reservas
        //eventPublisher.publish(new OrderCancelledEvent(order.getOrderId()));
    }
}
