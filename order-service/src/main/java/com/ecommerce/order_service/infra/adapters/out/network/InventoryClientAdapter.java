package com.ecommerce.order_service.infra.adapters.out.network;

import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.ports.out.InventoryClientPort;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.ReservationResponse;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.StockItemRequest;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.StockReservationRequest;
import com.ecommerce.order_service.infra.adapters.out.network.feign.ports.InventoryFeignClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InventoryClientAdapter implements InventoryClientPort {

    private final InventoryFeignClient feignClient;

    public InventoryClientAdapter(InventoryFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public UUID reserveStock(UUID orderId, List<OrderItem> items) {
        List<StockItemRequest> requestItems = items.stream()
                .map(item -> new StockItemRequest(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        StockReservationRequest request = new StockReservationRequest(orderId, requestItems);

        try {
            log.debug("Solicitando reserva de stock en inventario para la orden: {}", orderId);
            ReservationResponse response = feignClient.reserveStock(request);
            log.info("Stock reservado exitosamente. ID de Reserva (Correlation): {}", response.reservationId());

            return response.reservationId();

        } catch (FeignException.Conflict ex) {
            log.warn("Rechazo por regla de negocio (Sin Stock) para orden {}: {}", orderId, ex.getMessage());
            // Pasamos de un 409 tecnico a una excepcion de dominio
            throw new DomainValidationException("Stock insuficiente para uno o más productos de la orden.");

        } catch (FeignException ex) {
            log.error("Caída de red o error interno en Inventory Service. Orden: {}", orderId);
            throw new RuntimeException("Falla de comunicación temporal con el gestor de inventarios", ex);
        }
    }

    @Override
    public void confirmReservation(UUID reservationId) {
        try {
            log.debug("Confirmando reserva definitiva: {}", reservationId);
            feignClient.confirmReservation(reservationId);
            log.info("Reserva {} confirmada y descontada del stock físico.", reservationId);
        } catch (FeignException ex) {
            log.error("Falla al confirmar la reserva {}. Detalle: {}", reservationId, ex.getMessage());
            // En un sistema real esto requeriria encolamentie para reintentarlo de forma asincrona
            throw new RuntimeException("Falla de red al confirmar la reserva de inventario", ex);
        }
    }

    @Override
    public void cancelReservation(UUID reservationId) {
        try {
            log.info("Iniciando transacción compensatoria para revertir reserva: {}", reservationId);
            feignClient.cancelReservation(reservationId);
            log.info("Compensación exitosa. Stock liberado para la reserva: {}", reservationId);
        } catch (FeignException ex) {
            log.error("ALERTA CRÍTICA Falla en compensación para reserva {}. Requiere intervención manual. Detalle: {}", reservationId, ex.getMessage());
            throw new RuntimeException("Falla crítica en reversión de inventario", ex);
        }
    }
}
