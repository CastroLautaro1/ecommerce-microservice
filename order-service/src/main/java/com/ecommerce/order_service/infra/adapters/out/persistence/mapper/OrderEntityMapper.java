package com.ecommerce.order_service.infra.adapters.out.persistence.mapper;

import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OrderItemJpaEntity;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OrderJpaEntity;
import org.mapstruct.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderEntityMapper {

    // --- Reconstitucion: JPA -> Dominio Puro ---
    Order toDomain(OrderJpaEntity entity);

    OrderItem toDomainOrderItem(OrderItemJpaEntity entity);

    // --- Creacion: Dominio Puro -> JPA (Nuevos Registros) ---
    @Mapping(target = "reservationId", ignore = true)
    OrderJpaEntity toJpaEntity(Order domain);

    @Mapping(target = "order", ignore = true) // Evita bucle bidireccional en JPA
    @Mapping(target = "id", ignore = true)    // El ID lo genera la base de datos
    OrderItemJpaEntity toJpaEntityOrderItem(OrderItem domain);

    // --- Mutacion: Sincronización Fina de Estado (Dominio -> JPA Existente) ---

    /**
     * Mapea mutaciones de estado protegiendo el PersistentBag de Hibernate.
     * Dado que los OrderItem operan como Value Objects sin ID de persistencia en el dominio,
     * se utiliza el productId como clave de identidad local para evitar operaciones orphan-removal.
     */
    default void updateEntityFromDomain(Order domain, @MappingTarget OrderJpaEntity entity) {
        if (domain == null || entity == null) return;

        // 1. Actualización de propiedades escalares
        entity.setStatus(domain.getStatus().name());
        entity.setTotalAmount(domain.getTotalAmount());
        // Nota: createdAt, userId y orderId generalmente son inmutables tras la creación.

        if (domain.getItems() == null) return;

        // 2. Mapa de búsqueda usando productId como Identificador Local
        Map<Long, OrderItemJpaEntity> existingItems = entity.getItems().stream()
                .collect(Collectors.toMap(OrderItemJpaEntity::getProductId, Function.identity()));

        // 3. Estrategia "Merge" para la colección anidada
        for (OrderItem itemDomain : domain.getItems()) {
            OrderItemJpaEntity itemEntity = existingItems.get(itemDomain.getProductId());

            if (itemEntity != null) {
                // Existe en la orden actual: Mutamos estado (ej. ajuste de cantidad o precio)
                itemEntity.setQuantity(itemDomain.getQuantity());
                itemEntity.setUnitPrice(itemDomain.getUnitPrice());
                itemEntity.setProductName(itemDomain.getProductName());
            } else {
                // Es un ítem nuevo agregado a la orden existente
                OrderItemJpaEntity newItemEntity = toJpaEntityOrderItem(itemDomain);
                // Se asume que OrderJpaEntity tiene el método addItem(OrderItemJpaEntity)
                entity.addItem(newItemEntity);
            }
        }
    }

}
