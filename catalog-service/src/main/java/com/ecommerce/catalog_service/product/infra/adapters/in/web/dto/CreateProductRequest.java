package com.ecommerce.catalog_service.product.infra.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        Long categoryId,
        List<String> imageUrls
) {
}
