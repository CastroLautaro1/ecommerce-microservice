package com.ecommerce.inventory_service.infra.adapter.out.persistence.mapper;

import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.models.Reservation;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.InventoryJpaEntity;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.ReservationJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {

    // --- DE ENTIDAD JPA A DOMINIO PURO ---
    public Inventory toDomain(InventoryJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        // Mapeamos la lista de reservas internas usando el constructor de reconstrucción
        List<Reservation> domainReservations = jpaEntity.getReservations().stream()
                .map(this::toDomainReservation)
                .collect(Collectors.toList());

        // Reconstruimos el Agregado raíz
        return new Inventory(
                jpaEntity.getId(),
                jpaEntity.getProductId(),
                jpaEntity.getSku(),
                jpaEntity.getTotalStock(),
                jpaEntity.getAvailableStock(),
                domainReservations
        );
    }

    private Reservation toDomainReservation(ReservationJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;

        return new Reservation(
                jpaEntity.getReservationId(),
                jpaEntity.getOrderId(),
                jpaEntity.getQuantity(),
                jpaEntity.getStatus(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getExpiresAt()
        );
    }

    // --- DE DOMINIO PURO A ENTIDAD JPA ---
    public InventoryJpaEntity toJpaEntity(Inventory domain) {
        if (domain == null) return null;

        InventoryJpaEntity jpaEntity = new InventoryJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setProductId(domain.getProductId());
        jpaEntity.setSku(domain.getSku());
        jpaEntity.setTotalStock(domain.getTotalStock());
        jpaEntity.setAvailableStock(domain.getAvailableStock());

        // Mapeamos las reservas y aseguramos la sincronía bidireccional en JPA
        if (domain.getReservations() != null) {
            domain.getReservations().forEach(res -> {
                ReservationJpaEntity resJpa = toJpaEntityReservation(res);
                jpaEntity.addReservation(resJpa);
            });
        }

        return jpaEntity;
    }

    private ReservationJpaEntity toJpaEntityReservation(Reservation domain) {
        if (domain == null) return null;

        ReservationJpaEntity jpaEntity = new ReservationJpaEntity();
        jpaEntity.setReservationId(domain.getReservationId());
        jpaEntity.setOrderId(domain.getOrderId());
        jpaEntity.setQuantity(domain.getQuantity());
        jpaEntity.setStatus(domain.getStatus());
        jpaEntity.setCreatedAt(domain.getCreatedAt());
        jpaEntity.setExpiresAt(domain.getExpiresAt());

        return jpaEntity;
    }

    // Mapeo con Estado para proteger la integridad relacional de JPA
    public void updateEntityFromDomain(InventoryJpaEntity entity, Inventory domain) {
        // Actualizamos propiedades raiz
        entity.setAvailableStock(domain.getAvailableStock());
        entity.setTotalStock(domain.getTotalStock());

        // Se actualizan las reservas anidadas sin perder las referencias de JPA
        Map<UUID, ReservationJpaEntity> existingReservations = entity.getReservations().stream()
                .collect(Collectors.toMap(ReservationJpaEntity::getReservationId, Function.identity()));

        for (Reservation resDomain : domain.getReservations()) {
            ReservationJpaEntity resEntity = existingReservations.get(resDomain.getReservationId());

            if (resEntity != null) {
                // Actualiza la entidad existente -> Genera un UPDATE en SQL
                resEntity.setStatus(resDomain.getStatus());
                resEntity.setQuantity(resDomain.getQuantity());
                resEntity.setExpiresAt(resDomain.getExpiresAt());
            } else {
                // Si la reserva en el dominio es nueva, la instanciamos y la vinculamos -> Genera un INSERT en SQL
                ReservationJpaEntity newRes = new ReservationJpaEntity();
                newRes.setReservationId(resDomain.getReservationId());
                newRes.setOrderId(resDomain.getOrderId());
                newRes.setQuantity(resDomain.getQuantity());
                newRes.setStatus(resDomain.getStatus());
                newRes.setCreatedAt(resDomain.getCreatedAt());
                newRes.setExpiresAt(resDomain.getExpiresAt());

                entity.addReservation(newRes);
            }
        }
    }
}
