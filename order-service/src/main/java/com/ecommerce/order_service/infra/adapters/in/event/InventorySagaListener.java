package com.ecommerce.order_service.infra.adapters.in.event;

import com.ecommerce.order_service.domain.event.OrderCancelledEvent;
import com.ecommerce.order_service.domain.event.OrderConfirmedEvent;
import com.ecommerce.order_service.domain.event.OrderPendingEvent;
import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.ports.in.RejectOrderUseCase;
import com.ecommerce.order_service.infra.adapters.out.network.InventoryClientAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class InventorySagaListener {

    private final InventoryClientAdapter inventoryClient;
    private final RejectOrderUseCase rejectOrder;

    public InventorySagaListener(InventoryClientAdapter inventoryClient, RejectOrderUseCase rejectOrder) {
        this.inventoryClient = inventoryClient;
        this.rejectOrder = rejectOrder;
    }

    @Async("sagaTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // Solo se ejecuta si el INSERT de la Orden tuvo éxito
    public void handleOrderPending(OrderPendingEvent event) {
        try {
            inventoryClient.reserveStock(event.orderId(), event.items());

        } catch (DomainValidationException e) {
            // Se catchean las reglas de negocio rotas (Ej: No hay stock)
            log.info("Compensando orden {}. Motivo: {}", event.orderId(), e.getMessage());

            rejectOrder.execute(event.orderId()); // Mutamos estado a REJECTED

        } catch (Exception e) {
            // Se catchean los errores del sistema (Ej: servidor caido, timeout, etc)
            log.error("Fallo técnico de infraestructura al procesar la orden {}. Iniciando compensación por degradación.", event.orderId(), e);

            rejectOrder.execute(event.orderId()); // Mutamos estado a REJECTED
        }
    }

    // Escucha la confirmación
    @Async("sagaTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        try {
            inventoryClient.confirmReservation(event.orderId());
            log.info("Notificación de confirmación enviada al inventario para la orden {}", event.orderId());
        } catch (Exception e) {
            // A diferencia del PENDING, si esto falla, la orden YA ESTÁ confirmada.
            // Aca no se compensa la orden, se requiere un mecanismo de REINTENTO (Retry)
            // o un Dead Letter Queue (DLQ) para asegurar que el inventario se entere eventualmente.
            log.error("Fallo al confirmar reserva en inventario para orden {}. Requiere reintento manual o automático.", event.orderId(), e);
        }
    }

    // Escucha la cancelación
    @Async("sagaTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancelled(OrderCancelledEvent event) {
        try {
            inventoryClient.cancelReservation(event.orderId());
            log.info("Notificación de cancelación enviada al inventario para la orden {}", event.orderId());
        } catch (Exception e) {
            // En caso de fallar es el mismo caso de arriba. Consistencia Eventual
            log.error("Fallo al cancelar reserva en inventario para orden {}. El stock quedó retenido temporalmente.", event.orderId(), e);
        }
    }
}
