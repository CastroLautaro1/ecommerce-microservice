package com.ecommerce.order_service.infra.adapters.out.network.feign.ports;

import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.ReservationResponse;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.StockReservationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "inventory-service", url = "${application.services.inventory.url}")
public interface InventoryFeignClient {

    @PostMapping("/api/v1/inventories/reserve")
    ReservationResponse reserveStock(@RequestBody StockReservationRequest request);

    @PutMapping("/api/v1/reservations/orders/{orderId}/confirm")
    void confirmReservation(@PathVariable("orderId") UUID orderId);

    @PutMapping("/api/v1/reservations/orders/{orderId}/cancel")
    void cancelReservation(@PathVariable("orderId") UUID orderId);

//    @PutMapping("/api/v1/inventories/reservations/{reservationId}/confirm")
//    void confirmReservation(@PathVariable("reservationId") UUID reservationId);
//
//    @PutMapping("/api/v1/inventories/reservations/{reservationId}/cancel")
//    void cancelReservation(@PathVariable("reservationId") UUID reservationId);


}
