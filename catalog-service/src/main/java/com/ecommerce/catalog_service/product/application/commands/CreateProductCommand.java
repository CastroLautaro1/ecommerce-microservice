package com.ecommerce.catalog_service.product.application.commands;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        UUID sellerId,
        Long categoryId,
        List<String> imageUrls // Solo las urls, el dominio decide cual es la principal
) {
}
