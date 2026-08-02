package com.ecommerce.catalog_service.product.application.commands;


import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        Long sellerId,
        Long categoryId,
        List<String> imageUrls // Solo las urls, el dominio decide cual es la principal
) {
}
