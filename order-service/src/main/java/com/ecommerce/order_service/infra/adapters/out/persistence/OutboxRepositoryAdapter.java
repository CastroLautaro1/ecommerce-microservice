package com.ecommerce.order_service.infra.adapters.out.persistence;

import com.ecommerce.order_service.domain.models.OutboxEvent;
import com.ecommerce.order_service.domain.ports.out.OutboxRepositoryPort;
import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OutboxJpaEntity;
import com.ecommerce.order_service.infra.adapters.out.persistence.mapper.OutboxEntityMapper;
import com.ecommerce.order_service.infra.adapters.out.persistence.repository.SpringDataOutboxRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OutboxRepositoryAdapter implements OutboxRepositoryPort {

    private final SpringDataOutboxRepository repository;
    private final OutboxEntityMapper mapper;

    public OutboxRepositoryAdapter(SpringDataOutboxRepository repository, OutboxEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(OutboxEvent event) {
        OutboxJpaEntity entity = mapper.toEntity(event);
        repository.save(entity);
    }

    @Override
    public List<OutboxEvent> findPendingEvents() {
        String pendingStatus = OutboxEvent.Status.PENDING.name();

        return repository.findByStatusOrderByCreatedAtAsc(pendingStatus)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
