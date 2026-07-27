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
        ProductJpaEntity entity = mapToJpaEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public List<Product> findActiveProducts() {
        return jpaRepository.findByActiveTrue().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findActiveProductsByCategory(Long categoryId) {
        return jpaRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> searchActiveProductsByName(String keyword) {
        return jpaRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findActiveProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return jpaRepository.findByPriceAmountBetweenAndActiveTrue(minPrice, maxPrice).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    // --- Métodos privados de mapeo (Traducción manual conceptual) ---

    private Product mapToDomain(ProductJpaEntity entity) {
        if (entity == null) return null;

        Price price = null;
        if (entity.getPrice() != null) {
            price = new Price(entity.getPrice().getAmount(), entity.getPrice().getCurrency());
        }

        List<ProductImage> images = new ArrayList<>();
        if (entity.getImages() != null) {
            images = entity.getImages().stream()
                    .map(img -> new ProductImage(img.getUrl(), img.isMain()))
                    .collect(Collectors.toList());
        }

        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                price,
                entity.getSellerId(),
                entity.getCategoryId(),
                images,
                entity.isActive(),
                entity.getCreatedAt()
        );
    }

    private ProductJpaEntity mapToJpaEntity(Product domain) {
        if (domain == null) return null;

        ProductJpaEntity entity = new ProductJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setSellerId(domain.getSellerId());
        entity.setCategoryId(domain.getCategoryId());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());

        if (domain.getPrice() != null) {
            entity.setPrice(new PriceEmbeddable(domain.getPrice().amount(), domain.getPrice().currency()));
        }

        if (domain.getImages() != null) {
            List<ImageEmbeddable> embeddableImages = domain.getImages().stream()
                    .map(img -> new ImageEmbeddable(img.url(), img.isMain()))
                    .collect(Collectors.toList());
            entity.setImages(embeddableImages);
        }

        return entity;
    }
}
