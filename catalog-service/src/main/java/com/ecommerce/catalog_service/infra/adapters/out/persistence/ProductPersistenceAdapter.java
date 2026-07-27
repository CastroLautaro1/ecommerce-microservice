package com.ecommerce.catalog_service.infra.adapters.out.persistence;

import com.ecommerce.catalog_service.domain.models.Price;
import com.ecommerce.catalog_service.domain.models.Product;
import com.ecommerce.catalog_service.domain.models.ProductImage;
import com.ecommerce.catalog_service.domain.ports.out.ProductRepositoryPort;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.entity.ImageEmbeddable;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.entity.PriceEmbeddable;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.entity.ProductJpaEntity;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.mapper.ProductEntityMapper;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.repository.SpringDataProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository jpaRepository;
    private final ProductEntityMapper productMapper;

    public ProductPersistenceAdapter(SpringDataProductRepository jpaRepository, ProductEntityMapper productMapper) {
        this.jpaRepository = jpaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = productMapper.toJpaEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return productMapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(productMapper::toDomainModel);
    }

    @Override
    public List<Product> findActiveProducts() {
        return jpaRepository.findByActiveTrue().stream()
                .map(productMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findActiveProductsByCategory(Long categoryId) {
        return jpaRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(productMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchActiveProductsByName(String keyword) {
        return jpaRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword).stream()
                .map(productMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findActiveProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return jpaRepository.findByPriceAmountBetweenAndActiveTrue(minPrice, maxPrice).stream()
                .map(productMapper::toDomainModel)
                .collect(Collectors.toList());
    }
}
