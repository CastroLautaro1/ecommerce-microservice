package com.ecommerce.catalog_service.infra.adapters.out.persistence.mapper;

import com.ecommerce.catalog_service.domain.models.Price;
import com.ecommerce.catalog_service.domain.models.Product;
import com.ecommerce.catalog_service.domain.models.ProductImage;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.entity.ImageEmbeddable;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.entity.PriceEmbeddable;
import com.ecommerce.catalog_service.infra.adapters.out.persistence.entity.ProductJpaEntity;
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
