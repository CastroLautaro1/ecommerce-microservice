package com.ecommerce.inventory_service.infra.adapter.in.web;

import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.in.*;
import com.ecommerce.inventory_service.infra.adapter.in.web.dto.CreateInventoryRequest;
import com.ecommerce.inventory_service.infra.adapter.in.web.dto.InventoryResponse;
import com.ecommerce.inventory_service.infra.adapter.in.web.dto.ReservationResponse;
import com.ecommerce.inventory_service.infra.adapter.in.web.dto.ReserveStockRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventories")
public class InventoryController {

    private final CreateInventoryUseCase createInventoryUseCase;
    private final ReserveStockUseCase reserveStockUseCase;
    private final ConfirmOrderReservationsUseCase confirmOrderReservationsUseCase;
    private final CancelOrderReservationsUseCase cancelOrderReservationsUseCase;

    public InventoryController(
            CreateInventoryUseCase createInventoryUseCase,
            ReserveStockUseCase reserveStockUseCase,
            ConfirmOrderReservationsUseCase confirmOrderReservationsUseCase,
            CancelOrderReservationsUseCase cancelOrderReservationsUseCase) {
        this.createInventoryUseCase = createInventoryUseCase;
        this.reserveStockUseCase = reserveStockUseCase;
        this.confirmOrderReservationsUseCase = confirmOrderReservationsUseCase;
        this.cancelOrderReservationsUseCase = cancelOrderReservationsUseCase;
    }

    // Inicializar el inventario de un producto nuevo
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@RequestBody CreateInventoryRequest request) {
        CreateInventoryCommand command = new CreateInventoryCommand(
                request.productId(),
                request.sku(),
                request.initialStock()
        );

        Inventory savedInventory = createInventoryUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryResponse.fromDomain(savedInventory));
    }

    // Bloquear stock temporalmente (Llamado por el Order-Service en el Checkout)
    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserveStock(@RequestBody ReserveStockRequest request) {
        ReserveStockCommand command = new ReserveStockCommand(
                request.orderId(),
                request.items().stream()
                        .map(i -> new ItemReservationCommand(i.productId(), i.quantity()))
                        .collect(Collectors.toUnmodifiableList())
        );

        UUID reservationId = reserveStockUseCase.execute(command);

        ReservationResponse response = new ReservationResponse(
                reservationId,
                "Stock reservado con éxito por 15 minutos. Pendiente de confirmación de pago."
        );

        return ResponseEntity.ok(response);
    }

    // Confirma reservas en bloque
    @PutMapping("/orders/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrderReservations(@PathVariable UUID orderId) {
        // Si no hay stock o hay un estado inválido, el dominio lanzará una excepción (ej. InsufficientTotalStockException)
        confirmOrderReservationsUseCase.execute(orderId);

        return ResponseEntity.noContent().build();
    }

    // Cancelar reservas en bloque
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrderReservations(@PathVariable UUID orderId) {
        // El caso de uso buscará todas las reservas de esta orden y las liberará atómicamente.
        cancelOrderReservationsUseCase.execute(orderId);

        return ResponseEntity.noContent().build();
    }

//    // Confirmar la reserva (Se llama cuando el pago es exitoso)
//    @PutMapping("/reservations/{reservationId}/confirm")
//    public ResponseEntity<Void> confirmReservation(@PathVariable UUID reservationId) {
//        confirmOrderReservationsUseCase.execute(reservationId);
//
//        // Todo salio bien, no hay un JSON de respuesta que devolver
//        return ResponseEntity.noContent().build();
//    }
//
//    // Cancelar/Liberar la reserva (Se llama cuando el pago falla o expira)
//    @PutMapping("/reservations/{reservationId}/cancel")
//    public ResponseEntity<Void> cancelReservation(@PathVariable UUID reservationId) {
//        cancelOrderReservationsUseCase.execute(reservationId);
//
//        return ResponseEntity.noContent().build();
//    }


}
