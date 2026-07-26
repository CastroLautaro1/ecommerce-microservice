package com.ecommerce.order_service.application.services;

import com.ecommerce.order_service.domain.exceptions.OrderNotFoundException;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.ports.in.RejectOrderUseCase;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RejectOrderService implements RejectOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public RejectOrderService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void execute(UUID orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.reject();

        orderRepository.save(order);
    }
}
