package com.ecommerce.order_service.infra.adapters.in.web;

import com.ecommerce.order_service.application.commands.CreateOrderCommand;
import com.ecommerce.order_service.application.commands.OrderItemCommand;
import com.ecommerce.order_service.domain.models.Order;
import com.ecommerce.order_service.domain.ports.in.CreateOrderUseCase;
import com.ecommerce.order_service.infra.adapters.in.web.dto.CreateOrderRequest;
import com.ecommerce.order_service.infra.adapters.in.web.dto.OrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        // Mapeo de DTO de Infraestructura a Command de Aplicación
        CreateOrderCommand command = new CreateOrderCommand(
                UUID.randomUUID(), // Generamos el ID de la orden en la entrada
                request.userId(),
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
}
