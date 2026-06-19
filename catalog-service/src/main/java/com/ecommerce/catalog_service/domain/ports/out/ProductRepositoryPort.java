package com.ecommerce.catalog_service.domain.ports.out;

import com.ecommerce.catalog_service.domain.models.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findActiveProducts();
    List<Product> findActiveProductsByCategory(Long categoryId);
    List<Product> searchActiveProductsByName(String keyword);
    List<Product> findActiveProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
