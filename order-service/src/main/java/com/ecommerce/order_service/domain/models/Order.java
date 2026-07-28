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
    public static Order registerOrder(UUID orderId, Long userId, List<OrderItem> items) {
        if (orderId == null) throw new DomainValidationException("El OrderId (UUID) es obligatorio para la idempotencia");
        if (userId == null) throw new DomainValidationException("El ID de usuario es obligatorio");
        if (items == null || items.isEmpty()) throw new DomainValidationException("La orden no puede estar vacía");

        return new Order(
                null,
                orderId,
                userId,
                OrderStatus.PENDING, // Pendiente por defecto
                Instant.now(),
                items
        );
    }

    // Constructor para reconstruccion desde la BdD
    public Order(Long id, UUID orderId, Long userId, OrderStatus status, Instant createdAt, List<OrderItem> items) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.items = new ArrayList<>(items);
        this.totalAmount = calculateTotalAmount(items);
    }

    // --- LÓGICA DE NEGOCIO ---

    private static BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::calculateSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        if (this.status == OrderStatus.CONFIRMED) {
            return; // Idempotencia: el sistema distribuido pudo enviar el evento dos veces
        }
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    String.format("Transición inválida. No se puede confirmar una orden en estado %s.", this.status)
            );
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == OrderStatus.CANCELLED) {
            return; // Idempotencia
        }
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    String.format("Transición inválida. No se puede cancelar una orden en estado %s.", this.status)
            );
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void reject() {
        if (this.status == OrderStatus.REJECTED) {
            return; // Idempotencia
        }
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException(
                    String.format("Transición inválida. No se puede rechazar una orden en estado %s.", this.status)
            );
        }
        this.status = OrderStatus.REJECTED;
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
