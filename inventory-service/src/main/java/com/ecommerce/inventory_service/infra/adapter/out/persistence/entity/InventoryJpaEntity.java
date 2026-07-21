package com.ecommerce.inventory_service.infra.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventories")
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @Column(name = "total_stock", nullable = false)
    private int totalStock;

    @Column(name = "available_stock", nullable = false)
    private int availableStock;

    // Un producto puede tener muchas reservas históricas o activas
    @OneToMany(
            mappedBy = "inventory",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    private List<ReservationJpaEntity> reservations = new ArrayList<>();

    public InventoryJpaEntity() {}

    public InventoryJpaEntity(Long id, Long productId, String sku, int totalStock, int availableStock, List<ReservationJpaEntity> reservations) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.reservations = reservations;
    }

    // Método utilitario para mantener la sincronía bidireccional en JPA
    public void addReservation(ReservationJpaEntity reservation) {
        reservations.add(reservation);
        reservation.setInventory(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public List<ReservationJpaEntity> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationJpaEntity> reservations) {
        this.reservations = reservations;
    }
}
