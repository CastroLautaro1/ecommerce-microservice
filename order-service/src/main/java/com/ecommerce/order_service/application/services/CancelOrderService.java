package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.domain.event.OrderCancelledEvent;
import com.ecommerce.order_service.domain.exceptions.OrderNotFoundException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.EventPublisherPort;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelOrderService implements CancelOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final EventPublisherPort eventPublisher;

    public CancelOrderService(OrderRepositoryPort orderRepository, EventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.cancel();
        orderRepository.save(order);

        // Llamada al listener para cancelar las reservas
        eventPublisher.publish(new OrderCancelledEvent(order.getOrderId()));
    }
}
