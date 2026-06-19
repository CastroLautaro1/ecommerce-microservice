package com.ecommerce.catalog_service.domain.ports.in;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductCommand(
        Long productId,
        Long requestingUserId,
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        Long categoryId,
        List<String> imageUrls
) {
}
