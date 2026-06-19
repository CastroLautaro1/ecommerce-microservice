package com.ecommerce.catalog_service.infra.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(
        Long requestingUserId, 
        String name,
        String description,
        BigDecimal priceAmount,
        String priceCurrency,
        Long categoryId,
        List<String> imageUrls
) {
}
