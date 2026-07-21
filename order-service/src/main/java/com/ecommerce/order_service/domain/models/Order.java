package com.ecommerce.order_service.domain.models;

import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.exceptions.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {
    private Long id;
    private final UUID orderId; // UUID para Idempotencia
    private final Long userId; // Id del comprador
    private OrderStatus status;
    private final Instant createdAt;
    private final List<OrderItem> items;
    private BigDecimal totalAmount;

    // Constructor para crear una nueva Orden
    public Order(UUID orderId, Long userId, List<OrderItem> items) {
        if (orderId == null) throw new DomainValidationException("El OrderId (UUID) es obligatorio para la idempotencia");
        if (userId == null) throw new DomainValidationException("El ID de usuario es obligatorio");
        if (items == null || items.isEmpty()) throw new DomainValidationException("La orden no puede estar vacía");

        this.orderId = orderId;
        this.userId = userId;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotalAmount();
    }

    // Constructor para reconstruccion desde la BdD
    public Order(Long id, UUID orderId, Long userId, OrderStatus status, Instant createdAt, List<OrderItem> items) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotalAmount();
    }

    // --- LÓGICA DE NEGOCIO ---

    private BigDecimal calculateTotalAmount() {
        return this.items.stream()
                .map(OrderItem::calculateSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Solo se puede confirmar una orden en estado PENDING");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.CONFIRMED) {
            throw new InvalidOrderStateException("No se puede cancelar una orden que ya fue confirmada y despachada");
        }
        this.status = OrderStatus.CANCELLED;
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public Long getUserId() { return userId; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
}
