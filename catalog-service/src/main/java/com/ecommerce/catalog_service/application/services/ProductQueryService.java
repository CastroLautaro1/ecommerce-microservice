package com.ecommerce.catalog_service.application.services;

import com.ecommerce.catalog_service.domain.exceptions.ProductNotFoundException;
import com.ecommerce.catalog_service.domain.models.Product;
import com.ecommerce.catalog_service.domain.ports.in.ProductQueryUseCase;
import com.ecommerce.catalog_service.domain.ports.out.ProductRepositoryPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductQueryService implements ProductQueryUseCase {

    private final ProductRepositoryPort repository;

    public ProductQueryService(ProductRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Product getProductDetails(Long id) {
        return repository.findById(id)
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return repository.findActiveProducts();
    }

    @Override
    public List<Product> searchByName(String keyword) {
        return repository.searchActiveProductsByName(keyword);
    }

    @Override
    public List<Product> filterByCategory(Long categoryId) {
        return repository.findActiveProductsByCategory(categoryId);
    }

    @Override
    public List<Product> filterByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("El precio mínimo no puede ser mayor al máximo");
        }
        return repository.findActiveProductsByPriceRange(minPrice, maxPrice);
    }
}
