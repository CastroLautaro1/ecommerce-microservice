package com.ecommerce.catalog_service.product.domain.ports.in;

import com.ecommerce.catalog_service.product.domain.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductQueryUseCase {
    Product getProductDetails(Long id);
    List<Product> getAllActiveProducts();
    List<Product> searchByName(String keyword);
    List<Product> filterByCategory(Long categoryId);
    List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    boolean checkProductOwnership(Long productId, UUID sellerId);
}
