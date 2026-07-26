package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.domain.event.OrderConfirmedEvent;
import com.ecommerce.order_service.domain.exceptions.OrderNotFoundException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.ports.in.ConfirmOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.EventPublisherPort;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConfirmOrderService implements ConfirmOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    public ConfirmOrderService(OrderRepositoryPort orderRepository, EventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.confirm();
        orderRepository.save(order);

        // Llamada al listener para confirmar las reservas
        eventPublisher.publish(new OrderConfirmedEvent(orderId));
    }
}
