package com.ecommerce.order_service.domain.ports.out;

import com.ecommerce.order_service.domain.models.OutboxEvent;

import java.util.List;

public interface OutboxRepositoryPort {

    void save(OutboxEvent event);

    // Lo utiliza el Job para recuperar los eventos pendientes de procesar, ordenados del más antiguo al más reciente.
    List<OutboxEvent> findPendingEvents();
}
