package com.ecommerce.catalog_service.infra.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        Long sellerId,
        Long categoryId,
        List<String> imageUrls
) {
}
