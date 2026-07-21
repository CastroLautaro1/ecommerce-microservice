package com.ecommerce.inventory_service.infra.adapter.out.persistence.repository;

import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.InventoryJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataInventoryRepository extends JpaRepository<InventoryJpaEntity, Long> {
    Optional<InventoryJpaEntity> findByProductId(Long productId);

    Optional<InventoryJpaEntity> findBySku(String sku);

    @Query("SELECT DISTINCT i FROM InventoryJpaEntity i JOIN i.reservations r WHERE r.orderId = :orderId")
    List<InventoryJpaEntity> findByReservationsOrderId(@Param("orderId") UUID orderId);

    // Postgre agrega un FOR UPDATE al final del SQL, para que nadie mas pueda leer o escribir
    // sobre esta fila hasta que la transaccion termine
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryJpaEntity i WHERE i.productId = :productId")
    Optional<InventoryJpaEntity> findByProductIdWithLock(@Param("productId") Long productId);

    // JOIN con la lista de reservas para encontrar el inventario padre de un UUID
    @Query("SELECT i FROM InventoryJpaEntity i JOIN i.reservations r WHERE r.reservationId = :reservationId")
    Optional<InventoryJpaEntity> findByReservationId(@Param("reservationId") UUID reservationId);
}
