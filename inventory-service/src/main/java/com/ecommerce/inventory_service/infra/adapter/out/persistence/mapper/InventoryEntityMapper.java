package com.ecommerce.inventory_service.infra.adapter.out.persistence.mapper;

import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.models.Reservation;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.InventoryJpaEntity;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.ReservationJpaEntity;
import org.mapstruct.*;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface InventoryEntityMapper {

    // --- Reconstitucion: JPA -> Dominio Puro ---
    Inventory toDomain(InventoryJpaEntity entity);

    Reservation toDomainReservation(ReservationJpaEntity entity);

    // --- Creacion: Dominio Puro -> JPA (Nuevos Registros) ---
    InventoryJpaEntity toJpaEntity(Inventory domain);

    @Mapping(target = "inventory", ignore = true) // Previene ciclos infinitos en relaciones bidireccionales
    ReservationJpaEntity toJpaEntityReservation(Reservation domain);

    // --- Mutacion: Sincronización Fina de Estado (Dominio -> JPA Existente) ---

    /**
     * Mapeo de estado para proteger la integridad relacional de Hibernate.
     * Se utiliza un método 'default' porque MapStruct nativo no puede realizar
     * un "Upsert" inteligente (Update por ID / Insert de nuevos) en colecciones
     * sin sobreescribir las referencias de memoria controladas por el EntityManager.
     *
     * @param domain El estado actual del Agregado en memoria.
     * @param entity La entidad atada al Contexto de Persistencia (inyectada por MapStruct).
     */
    default void updateEntityFromDomain(Inventory domain, @MappingTarget InventoryJpaEntity entity) {
        if (domain == null || entity == null) return;

        // 1. Actualización de propiedades raíz (Dirty checking simple)
        entity.setAvailableStock(domain.getAvailableStock());
        entity.setTotalStock(domain.getTotalStock());

        if (domain.getReservations() == null) return;

        // 2. Mapa de búsqueda en memoria O(1) para evitar N iteraciones
        Map<UUID, ReservationJpaEntity> existingReservations = entity.getReservations().stream()
                .collect(Collectors.toMap(ReservationJpaEntity::getReservationId, Function.identity()));

        // 3. Estrategia "Merge" manual para proteger la colección anidada
        for (Reservation resDomain : domain.getReservations()) {
            ReservationJpaEntity resEntity = existingReservations.get(resDomain.getReservationId());

            if (resEntity != null) {
                // Existe: Se mutan solo los campos necesarios.
                // Hibernate registrará esto como un UPDATE en la fase de flush().
                resEntity.setStatus(resDomain.getStatus());
                resEntity.setQuantity(resDomain.getQuantity());
                resEntity.setExpiresAt(resDomain.getExpiresAt());
            } else {
                // Es nueva: Se instancia, mapea e inyecta al contexto relacional.
                // Hibernate registrará esto como un INSERT en la fase de flush().
                ReservationJpaEntity newRes = toJpaEntityReservation(resDomain);
                entity.addReservation(newRes);
            }
        }
    }

}
