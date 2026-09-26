package com.ecommerce.order_service.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class OutboxEvent {

    private final UUID id;
    private final Long aggregateId;
    private final String aggregateType;
    private final String eventType; // Ej: 'ORDER_CONFIRMED', 'ORDER_CANCELLED'
    private final String payload; // JSON representando el estado
    private Status status;
    private String errorMessage;
    private final LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public enum Status {
        PENDING, COMPLETED, FAILED
    }

    // Constructor privado
    private OutboxEvent(UUID id, Long aggregateId, String aggregateType, String eventType,
                        String payload, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Constructor para instanciar un nuevo OutboxEvenat
    public static OutboxEvent createPending(Long aggregateId, String aggregateType,
                                            String eventType, String payload) {
        return new OutboxEvent(
                UUID.randomUUID(),
                aggregateId,
                aggregateType,
                eventType,
                payload,
                Status.PENDING,
                LocalDateTime.now()
        );
    }

    // Mutadores de estado para el Job de procesamiento
    public void markAsCompleted() {
        this.status = Status.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    public void updateErrorMessage(String error) {
        this.errorMessage = error;
    }

    public void markAsFailed(String error) {
        this.status = Status.FAILED;
        this.errorMessage = error;
        this.processedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
