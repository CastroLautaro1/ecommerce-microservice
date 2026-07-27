package com.ecommerce.order_service.infra.adapters.out.persistence;

import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.models.OrderStatus;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OrderItemJpaEntity;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OrderJpaEntity;
import com.ecommerce.order_service.infra.adapters.out.persistence.mapper.OrderEntityMapper;
import com.ecommerce.order_service.infra.adapters.out.persistence.repository.SpringDataOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository orderRepository;
    private final OrderEntityMapper orderMapper;

    public OrderRepositoryAdapter(SpringDataOrderRepository orderRepository, OrderEntityMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = orderMapper.toJpaEntity(order);
        OrderJpaEntity savedEntity = orderRepository.save(entity);
        return orderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id).map(orderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByOrderId(UUID orderId) {
        return orderRepository.findByOrderId(orderId).map(orderMapper::toDomain);
    }
}
