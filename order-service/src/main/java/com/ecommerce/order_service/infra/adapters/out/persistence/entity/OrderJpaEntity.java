package com.ecommerce.order_service.infra.adapters.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true, updatable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

//    @Column(name = "reservation_id")
//    private UUID reservationId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // Relación bidireccional, el Aggregate Root controla el ciclo de vida de los items
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemJpaEntity> items = new ArrayList<>();

    public OrderJpaEntity() {
    }

    public OrderJpaEntity(Long id, UUID orderId, UUID userId, String status, Instant createdAt, BigDecimal totalAmount, List<OrderItemJpaEntity> items) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
//      this.reservationId = reservationId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public void addItem(OrderItemJpaEntity newItem) {
        newItem.setOrder(this);
        this.items.add(newItem);
    }

    public void addItems(List<OrderItemJpaEntity> newItems) {
        newItems.forEach(item -> {
            item.setOrder(this);
            this.items.add(item);
        });
    }

    // -- GETTERS Y SETTERS --

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

//    public UUID getReservationId() {
//        return reservationId;
//    }
//
//    public void setReservationId(UUID reservationId) {
//        this.reservationId = reservationId;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemJpaEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemJpaEntity> items) {
        this.items = items;
    }
}
