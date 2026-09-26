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
    public Order persistAndPublish(UUID orderId, UUID userId, List<OrderItem> hydratedItems) {
        if (orderRepository.findByOrderId(orderId).isPresent()) {
            throw new DomainValidationException("La orden con UUID " + orderId + " ya fue procesada.");
        }

        Order order = Order.registerOrder(orderId, userId, hydratedItems);
        orderRepository.save(order);

        eventPublisher.publish(new OrderPendingEvent(order.getOrderId(), order.getItems()));

        return order;
    }

}
