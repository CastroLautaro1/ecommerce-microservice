package com.ecommerce.catalog_service.product.domain.ports.out;

import com.ecommerce.catalog_service.product.domain.models.Product;

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
    boolean existsByName(String name); // Para crear un Producto
    boolean existsByNameAndIdNot(String name, Long excludedId); // Para actualizar un Producto
}
