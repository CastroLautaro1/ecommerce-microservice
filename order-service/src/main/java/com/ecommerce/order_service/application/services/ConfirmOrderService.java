package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.domain.event.OrderConfirmedEvent;
import com.ecommerce.order_service.domain.exceptions.OrderNotFoundException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OutboxEvent;
import com.ecommerce.order_service.domain.ports.in.ConfirmOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.EventPublisherPort;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import com.ecommerce.order_service.domain.ports.out.OutboxRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OutboxRepositoryPort outboxRepository;
    private final ObjectMapper objectMapper;
    //private final EventPublisherPort eventPublisher;

    public ConfirmOrderService(OrderRepositoryPort orderRepository, OutboxRepositoryPort outboxRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        //this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        // Confirmo el estado de la Orden y la guardo
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.confirm();
        orderRepository.save(order);

        try {
            // Se transforma el Snapshot en un JSON para guardar la información en el OutboxEvent
            String payload = objectMapper.writeValueAsString(order.toSnapshot());

            // Se crea y guarda el OutboxEvent para luego comunicarse con el Inventario
            OutboxEvent outboxEvent = OutboxEvent.createPending(
                    order.getOrderId(),
                    "ORDER",
                    "ORDER_CONFIRMED",
                    payload
            );
            outboxRepository.save(outboxEvent);

        } catch (Exception e) {
            throw new RuntimeException("Error fatal al serializar el payload del Outbox", e);
        }

        // Llamada al listener para confirmar las reservas
        // Ahora ya no emito eventos, porque todo pasa a depender del Outbox
        // eventPublisher.publish(new OrderConfirmedEvent(orderId));
    }
}
