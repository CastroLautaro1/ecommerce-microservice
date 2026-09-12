package com.ecommerce.catalog_service.product.application.services;

import com.ecommerce.catalog_service.product.domain.models.Product;
import com.ecommerce.catalog_service.product.domain.ports.in.ProductQueryUseCase;
import com.ecommerce.catalog_service.product.domain.ports.out.ProductRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
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

    @Override
    public boolean checkProductOwnership(Long productId, UUID sellerId) {
        return repository.existsByIdAndSellerIdAndIsActiveTrue(productId, sellerId);
    }
}
