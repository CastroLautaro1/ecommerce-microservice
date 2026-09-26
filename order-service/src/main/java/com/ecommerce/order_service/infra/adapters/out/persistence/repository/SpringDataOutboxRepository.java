package com.ecommerce.order_service.infra.adapters.out.persistence.repository;

import com.ecommerce.order_service.infra.adapters.out.persistence.entity.OutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataOutboxRepository extends JpaRepository<OutboxJpaEntity, Long> {

    List<OutboxJpaEntity> findByStatusOrderByCreatedAtAsc(String status);
}
