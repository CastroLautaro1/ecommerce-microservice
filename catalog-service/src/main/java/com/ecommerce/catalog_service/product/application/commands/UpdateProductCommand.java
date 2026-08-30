package com.ecommerce.catalog_service.product.application.commands;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateProductCommand(
        Long productId,
        UUID requestingUserId,
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        Long categoryId,
        List<String> imageUrls
) {
}
