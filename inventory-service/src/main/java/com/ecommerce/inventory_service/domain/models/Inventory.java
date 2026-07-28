package com.ecommerce.inventory_service.domain.models;

import com.ecommerce.inventory_service.domain.exceptions.DomainValidationException;
import com.ecommerce.inventory_service.domain.exceptions.InsufficientStockException;
import com.ecommerce.inventory_service.domain.exceptions.ReservationNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Inventory {
    private Long id;
    private final Long productId; // Referencia a el producto
    private final String sku;
    private int totalStock;
    private int availableStock;
    private final List<Reservation> reservations;

    // Factory Method para crear un inventario desde cero para un nuevo producto
    public static Inventory registerInventory(Long productId, String sku, int initialStock) {
        if (productId == null) throw new DomainValidationException("El ID del producto es obligatorio");
        if (sku == null || sku.isBlank()) throw new DomainValidationException("El SKU es obligatorio");
        if (initialStock < 0) throw new DomainValidationException("El stock inicial no puede ser negativo");

        return new Inventory(
                null,
                productId,
                sku.trim().toUpperCase(),
                initialStock,
                initialStock, // Al inicio, todo el stock está disponible
                new ArrayList<>()
        );
    }

    // Constructor para leer desde la BD
    public Inventory(Long id, Long productId, String sku, int totalStock, int availableStock, List<Reservation> reservations) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.reservations = new ArrayList<>(reservations);
    }

    // --- COMPORTAMIENTOS DE NEGOCIO  ---

     // RESERVAR STOCK: Se llama cuando el cliente le da al botón "Comprar".
     // Bloquea temporalmente las unidades para que nadie más pueda llevárselas.
    public Reservation reserve(UUID reservationId, UUID orderId, int quantity, Instant expiresAt) {
        if (quantity <= 0) {
            throw new DomainValidationException("La cantidad a reservar debe ser mayor a cero");
        }
        // No podemos prestar lo que no tenemos disponible
        if (quantity > this.availableStock) {
            throw new InsufficientStockException(this.sku, quantity, this.availableStock);
        }

        //Reservation newReservation = new Reservation(reservationId, orderId, quantity, expiresAt);
        Reservation newReservation = Reservation.registerReservation(reservationId, orderId, quantity, expiresAt);
        this.reservations.add(newReservation);

        // Descontamos únicamente del stock disponible, el físico (total) sigue intacto en el almacén
        this.availableStock -= quantity;

        return newReservation;
    }

    // Confirma todas las Reservas ligadas a una Orden
    public void confirmReservationsForOrder(UUID orderId) {
        List<Reservation> targetReservations = this.reservations.stream()
                .filter(r -> r.getOrderId().equals(orderId))
                .toList();

        for (Reservation res : targetReservations) {
            // Guardia de Dominio (Invariante del Agregado)
            if (this.totalStock < res.getQuantity()) {
                throw new InsufficientStockException(this.sku, res.getQuantity(), this.availableStock);
            }
            // Se reduce el stock fisico real porque la Orden ya fue confirmada
            this.totalStock -= res.getQuantity();

            res.confirm(); // El estado de la Reserva pasa a CONFIRMED
        }
    }

    // Cancela todas las Reservas ligadas a una Orden
    public void cancelReservationsForOrder(UUID orderId) {
        List<Reservation> targetReservations = this.reservations.stream()
                .filter(r -> r.getOrderId().equals(orderId))
                .toList();

        for (Reservation res : targetReservations) {
            // La cancelación/compensación devuelve el stock para que pueda ser comprado por otro
            this.availableStock += res.getQuantity();
            res.cancel(); // El estado de la Reserva pasa a CANCELED
        }
    }

     // CONFIRMAR RESERVA: Llamado por un webhook o el Order-Service cuando el pago fue aprobado.
     // El stock abandona definitivamente el almacén físico.
    public void confirmReservation(UUID reservationId) {
        Reservation reservation = findReservationById(reservationId);

        reservation.confirm();

        // Como el pago fue aprobado, se descuenta el stock fisico (totalStock)
        this.totalStock -= reservation.getQuantity();
    }

     // CANCELAR / LIBERAR RESERVA: Llamado si el pago falla, la orden se cancela o expira el tiempo.
     // Las unidades reservadas regresan al pool de stock disponible.
    public void cancelReservation(UUID reservationId) {
        Reservation reservation = findReservationById(reservationId);

        reservation.cancel();

        // Devolvemos las unidades al stock disponible (availableStock) para que otro cliente pueda comprarlas
        this.availableStock += reservation.getQuantity();
    }

     // AÑADIR STOCK (RESTOCK): Llamado cuando ingresa nueva mercaderia
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("La cantidad para recargar stock debe ser mayor a cero");
        }
        this.totalStock += quantity;
        this.availableStock += quantity;
    }

    // --- MÉTODOS AUXILIARES Y GETTERS ---

    private Reservation findReservationById(UUID reservationId) {
        return this.reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst()
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getSku() { return sku; }
    public int getTotalStock() { return totalStock; }
    public int getAvailableStock() { return availableStock; }
    // Devolvemos una lista inmutable para que nadie haga un inventory.getReservations().add(...) y rompa la matemática
    public List<Reservation> getReservations() { return Collections.unmodifiableList(reservations); }
}
