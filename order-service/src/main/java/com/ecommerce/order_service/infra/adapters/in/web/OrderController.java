package com.ecommerce.order_service.infra.adapters.in.web;

import com.ecommerce.order_service.application.commands.CreateOrderCommand;
import com.ecommerce.order_service.application.commands.OrderItemCommand;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.ports.in.CancelOrderUseCase;
import com.ecommerce.order_service.domain.ports.in.ConfirmOrderUseCase;
import com.ecommerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.ecommerce.order_service.infra.adapters.in.web.dto.CreateOrderRequest;
import com.ecommerce.order_service.infra.adapters.in.web.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, ConfirmOrderUseCase confirmOrderUseCase, CancelOrderUseCase cancelOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            @RequestHeader("X-User-Id") UUID requestingUserId
    ) {
        // Mapeo de DTO de Infraestructura a Command de Aplicación
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(), // Generamos el ID de la orden en la entrada
                requestingUserId,
                request.items().stream()
                        .map(item -> new OrderItemCommand(item.productId(), item.quantity()))
                        .collect(Collectors.toList())
        );

        // El caso de uso se encarga de crear la Orden
        Order order = createOrderUseCase.execute(command);

        // 201 más DTO de respuesta
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OrderResponse(order.getOrderId(), order.getStatus().name()));
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable UUID orderId) {
        confirmOrderUseCase.execute(orderId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SYSTEM')")
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable UUID orderId) {
        cancelOrderUseCase.execute(orderId);
        return ResponseEntity.noContent().build();
    }
}
