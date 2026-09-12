package com.ecommerce.catalog_service.product.infra.adapters.out.persistence.repository;

import com.ecommerce.catalog_service.product.infra.adapters.out.persistence.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, Long> {

    // "findByActiveTrue" - Cumple con el requisito: visualizar productos activos
    List<ProductJpaEntity> findByActiveTrue();

    // Cumple con el requisito: filtrar productos activos por su categoria
    List<ProductJpaEntity> findByCategoryIdAndActiveTrue(Long categoryId);

    // Cumple con el requisito: buscar producto activo por su nombre (Like / Contains)
    List<ProductJpaEntity> findByNameContainingIgnoreCaseAndActiveTrue(String keyword);

    // Cumple con el requisito: filtrar productos activos por un rango de precios
    List<ProductJpaEntity> findByPriceAmountBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsByIdAndSellerIdAndActiveTrue(Long productId, UUID sellerId);
}
