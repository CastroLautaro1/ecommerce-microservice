package com.ecommerce.order_service.infra.adapters.out.network;

import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.exceptions.ExternalServiceUnavailableException;
import com.ecommerce.order_service.domain.models.OrderItem;
import com.ecommerce.order_service.domain.ports.out.InventoryClientPort;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.ReservationResponse;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.StockItemRequest;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.StockReservationRequest;
import com.ecommerce.order_service.infra.adapters.out.network.feign.ports.InventoryFeignClient;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackForStockReservation")
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
            throw new ExternalServiceUnavailableException("Falla de comunicación temporal con el gestor de inventarios", ex);
        }
    }

    @Override
    @CircuitBreaker(name = "inventoryService")
    public void confirmReservation(UUID orderId) {
        try {
            log.debug("Confirmando reservas para la Orden: {}", orderId);
            feignClient.confirmReservation(orderId);
            log.info("Reserva/s para la Orden: {} confirmadas y descontadas del stock físico.", orderId);
        } catch (FeignException ex) {
            log.error("Falla al confirmar las reservas de la Orden {}. Detalle: {}", orderId, ex.getMessage());
            // En un sistema real esto requeriria encolamiento para reintentarlo de forma asincrona
            throw new ExternalServiceUnavailableException("Falla de red al confirmar la reserva de inventario", ex);
        }
    }

    @Override
    @CircuitBreaker(name = "inventoryService")
    public void cancelReservation(UUID orderId) {
        try {
            log.info("Iniciando transacción compensatoria para revertir las reservas de la Orden: {}", orderId);
            feignClient.cancelReservation(orderId);
            log.info("Compensación exitosa. Stock liberado para las reservas de la Orden: {}", orderId);
        } catch (FeignException ex) {
            log.error("ALERTA CRÍTICA Falla en compensación para las reservas de la Orden {}. Requiere intervención manual. Detalle: {}", orderId, ex.getMessage());
            throw new ExternalServiceUnavailableException("Falla crítica en reversión de inventario", ex);
        }
    }

    public UUID fallbackForStockReservation(UUID orderId, List<OrderItem> items, Throwable throwable) {
        throw new ExternalServiceUnavailableException(
                "El servicio de reservas del Inventario no se encuentra disponible. Por favor, intente nuevamente.",
                throwable
        );
    }
}
