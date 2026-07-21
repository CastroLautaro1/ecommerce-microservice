package com.ecommerce.inventory_service.infra.adapter.out.persistence.entity;

import com.ecommerce.inventory_service.domain.models.ReservationStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class ReservationJpaEntity {

    @Id
    @Column(name = "reservation_id", updatable = false, nullable = false)
    private UUID reservationId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryJpaEntity inventory;

    public ReservationJpaEntity() {}

    public ReservationJpaEntity(UUID reservationId, UUID orderId, int quantity, ReservationStatus status, Instant createdAt, Instant expiresAt, InventoryJpaEntity inventory) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.inventory = inventory;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public InventoryJpaEntity getInventory() {
        return inventory;
    }

    public void setInventory(InventoryJpaEntity inventory) {
        this.inventory = inventory;
    }
}
