package com.ecommerce.inventory_service.domain.models;

import com.ecommerce.inventory_service.domain.exceptions.DomainValidationException;
import com.ecommerce.inventory_service.domain.exceptions.InvalidReservationStateException;

import java.time.Instant;
import java.util.UUID;

public class Reservation {
    private final UUID reservationId;
    private final UUID orderId;
    private final int quantity;
    private ReservationStatus status;
    private final Instant createdAt;
    private final Instant expiresAt;

    public static Reservation registerReservation(UUID reservationId, UUID orderId, int quantity, Instant expiresAt) {
        if (reservationId == null) throw new DomainValidationException("El ID de la reserva es obligatorio");
        if (orderId == null) throw new DomainValidationException("El ID de la orden es obligatorio");
        if (quantity <= 0) throw new DomainValidationException("La cantidad a reservar debe ser mayor a cero");
        if (expiresAt == null || expiresAt.isBefore(Instant.now())) {
            throw new DomainValidationException("La fecha de expiración de la reserva es inválida");
        }

        return new Reservation(
                reservationId,
                orderId,
                quantity,
                ReservationStatus.ACTIVE, // Activa por defecto
                Instant.now(),
                expiresAt
        );
    }

    // Constructor de reconstrucción (Usado exclusivamente por el Mapeador de Infraestructura)
    public Reservation(UUID reservationId, UUID orderId, int quantity, ReservationStatus status, Instant createdAt, Instant expiresAt) {
        this.reservationId = reservationId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Comportamientos de cambio de estado protegidos
    public void confirm() {
        ensureIsActive();
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        ensureIsActive();
        this.status = ReservationStatus.CANCELLED;
    }

    public void expire() {
        ensureIsActive();
        this.status = ReservationStatus.EXPIRED;
    }

    public boolean isActive() {
        return this.status == ReservationStatus.ACTIVE;
    }

    private void ensureIsActive() {
        if (!isActive()) {
            throw new InvalidReservationStateException(
                    String.format("La reserva %s no está activa (Estado actual: %s)", reservationId, status)
            );
        }
    }

    // Getters imprescindibles para el Agregado
    public UUID getReservationId() { return reservationId; }
    public UUID getOrderId() { return orderId; }
    public int getQuantity() { return quantity; }
    public ReservationStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
}
