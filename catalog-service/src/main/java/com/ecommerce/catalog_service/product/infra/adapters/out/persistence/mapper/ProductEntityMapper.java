package com.ecommerce.catalog_service.product.infra.adapters.out.persistence.mapper;

import com.ecommerce.catalog_service.product.domain.models.*;
import com.ecommerce.catalog_service.product.infra.adapters.out.persistence.entity.ImageEmbeddable;
import com.ecommerce.catalog_service.product.infra.adapters.out.persistence.entity.PriceEmbeddable;
import com.ecommerce.catalog_service.product.infra.adapters.out.persistence.entity.ProductJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductEntityMapper {

    // --- Mapeo Principal, el Agregado Raiz ---

    // JPA -> Dominio
    Product toDomainModel(ProductJpaEntity entity);

    // Dominio -> JPA
    ProductJpaEntity toJpaEntity(Product domain);

    // --- Mapeos Delegados: Value Object y Entidades anidadas ---

    // Dominio -> JPA
    default String map(ProductName productName) {
        return productName != null ? productName.value() : null;
    }

    // JPA -> Dominio
    default ProductName mapToProductName(String name) {
        return name != null ? new ProductName(name) : null;
    }

    // Dominio -> JPA
    default String map(ProductDescription productDescription) {
        return productDescription != null ? productDescription.value() : null;
    }

    // JPA -> Dominio
    default ProductDescription mapToProductDescription(String description) {
        return description != null ? new ProductDescription(description) : null;
    }

    // Price
    Price toDomainPrice(PriceEmbeddable entityPrice);
    PriceEmbeddable toPriceEmbeddable(Price domainPrice);

    // Images
    ProductImage toDomainImage(ImageEmbeddable entityImage);
    ImageEmbeddable toImageEmbeddable(ProductImage domainImage);

    // Colecciones (es opcional declararlas explicitamente, pero buena práctica para claridad)
    List<ProductImage> toDomainImageList(List<ImageEmbeddable> entityImages);
    List<ImageEmbeddable> toImageEmbeddableList(List<ProductImage> domainImages);
}
