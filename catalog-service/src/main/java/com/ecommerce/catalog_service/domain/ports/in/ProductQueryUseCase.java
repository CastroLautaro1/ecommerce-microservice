package com.ecommerce.catalog_service.domain.ports.in;

import com.ecommerce.catalog_service.domain.models.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductQueryUseCase {
    Product getProductDetails(Long id);
    List<Product> getAllActiveProducts();
    List<Product> searchByName(String keyword);
    List<Product> filterByCategory(Long categoryId);
    List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
