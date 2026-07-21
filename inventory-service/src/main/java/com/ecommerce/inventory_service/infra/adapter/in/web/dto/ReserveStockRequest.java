package com.ecommerce.inventory_service.infra.adapter.in.web.dto;

// Este record era para cuando se reservaba un solo producto
//public record ReserveStockRequest(
//        Long orderId,
//        Long productId,
//        int quantity
//) {
//}

import java.util.List;
import java.util.UUID;

public record ReserveStockRequest(
        UUID orderId,
        List<ItemReservationDto> items
) {}

