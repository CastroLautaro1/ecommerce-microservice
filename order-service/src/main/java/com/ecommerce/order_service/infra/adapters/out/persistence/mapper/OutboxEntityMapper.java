package com.ecommerce.order_service.infra.adapters.out.persistence.mapper;

import com.ecommerce.order_service.domain.models.OutboxEvent;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OutboxJpaEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring"
)
public interface OutboxEntityMapper {

    OutboxEvent toDomain(OutboxJpaEntity entity);

    OutboxJpaEntity toEntity(OutboxEvent domain);

    List<OutboxEvent> toDomainList(List<OutboxJpaEntity> entities);

    List<OutboxJpaEntity> toEntityList(List<OutboxEvent> domain);
}
