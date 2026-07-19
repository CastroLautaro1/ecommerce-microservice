package com.ecommerce.order_service.infra.adapters.out.persistence.repository;

import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.models.OrderStatus;
import com.ecommerce.order_service.domain.ports.out.OrderRepositoryPort;
import com.ecommerce.order_service.infra.adapters.out.persistence.SpringDataOrderRepository;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OrderItemJpaEntity;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OrderJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository orderRepository;

    public OrderRepositoryAdapter(SpringDataOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapToJpaEntity(order);
        OrderJpaEntity savedEntity = orderRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Order> findByOrderId(UUID orderId) {
        return orderRepository.findByOrderId(orderId).map(this::mapToDomain);
    }

    // --- MAPPERS (Capa de Anticorrupción) ---

    private OrderJpaEntity mapToJpaEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        // Si el ID no es null, es un update
        if (order.getId() != null) {
            entity.setId(order.getId());
        }
        entity.setOrderId(order.getOrderId());
        entity.setUserId(order.getUserId());
        entity.setReservationId(order.getReservationId());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setTotalAmount(order.getTotalAmount());

        List<OrderItemJpaEntity> itemEntities = order.getItems().stream().map(item -> {
            OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
            itemEntity.setProductId(item.getProductId());
            itemEntity.setProductName(item.getProductName());
            itemEntity.setUnitPrice(item.getUnitPrice());
            itemEntity.setQuantity(item.getQuantity());
            return itemEntity;
        }).collect(Collectors.toList());

        entity.addItems(itemEntities);
        return entity;
    }

    private Order mapToDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(itemEntity -> new OrderItem(
                        itemEntity.getProductId(),
                        itemEntity.getProductName(),
                        itemEntity.getUnitPrice(),
                        itemEntity.getQuantity()
                ))
                .collect(Collectors.toList());

        return new Order(
                entity.getId(),
                entity.getOrderId(),
                entity.getUserId(),
                entity.getReservationId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                items
        );
    }
}
