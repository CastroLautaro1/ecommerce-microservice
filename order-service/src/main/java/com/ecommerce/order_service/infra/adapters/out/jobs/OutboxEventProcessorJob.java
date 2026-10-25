package com.ecommerce.order_service.infra.adapters.out.jobs;

import com.ecommerce.order_service.domain.exceptions.ExternalServiceUnavailableException;
import com.ecommerce.order_service.domain.models.OutboxEvent;
import com.ecommerce.order_service.domain.ports.out.OutboxRepositoryPort;
import com.ecommerce.order_service.infra.adapters.out.network.InventoryClientAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class OutboxEventProcessorJob {

    private final OutboxRepositoryPort outboxRepository;
    private final InventoryClientAdapter inventoryClient;

    public OutboxEventProcessorJob(OutboxRepositoryPort outboxRepository, InventoryClientAdapter inventoryClient) {
        this.outboxRepository = outboxRepository;
        this.inventoryClient = inventoryClient;
    }

    // Se ejecuta cada 30 segundos. spring.batch.job.enabled=false en el application.yml.
    @Scheduled(fixedDelay = 30000)
    public void processPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findPendingEvents();

        for (OutboxEvent event : pendingEvents) {
            processSingleEvent(event);
        }
    }

    // El REQUIRES_NEW asegura que si un evento falla, no bloquea ni revierte a los demás en el bucle
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleEvent(OutboxEvent event) {
        try {
            switch (event.getEventType()) {
                case "ORDER_CONFIRMED" -> inventoryClient.confirmReservation(event.getAggregateId());
                case "ORDER_CANCELLED" -> inventoryClient.cancelReservation(event.getAggregateId());
            }

            // Si la llamada HTTP tiene exito, se marca al evento como completado
            event.markAsCompleted();
            outboxRepository.save(event);
            log.info("Evento Outbox {} procesado exitosamente para la orden {}", event.getId(), event.getAggregateId());

        } catch (ExternalServiceUnavailableException e) {
            // Falla de red controlada (Circuit Breaker abierto o HTTP 5xx)
            // NO cambiamos el estado a FAILED. Lo dejamos PENDING para el próximo reintento.
            event.updateErrorMessage(e.getMessage());
            outboxRepository.save(event);
            log.warn("Reintento fallido para evento Outbox {}. Se reintentará en el próximo ciclo.", event.getId());

        } catch (Exception e) {
            // Falla grave inesperada (ej. NullPointerException, 400 Bad Request)
            // Aca si se marca como FAILED para requerir intervención humana (DLQ), este es el peor de los casos
            event.markAsFailed(e.getMessage());
            outboxRepository.save(event);
            log.error("Fallo crítico e irrecuperable procesando evento Outbox {}", event.getId(), e);
        }
    }
}
