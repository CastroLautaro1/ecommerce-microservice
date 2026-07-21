package com.ecommerce.order_service.infra.adapters.out.network;

import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.ports.out.CatalogClientPort;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.CatalogProductResponse;
import com.ecommerce.order_service.infra.adapters.out.network.feign.ports.CatalogFeignClient;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CatalogClientAdapter implements CatalogClientPort {

    private final CatalogFeignClient feignClient;

    public CatalogClientAdapter(CatalogFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public Optional<ProductSnapshot> getProductSnapshot(Long productId) {
        try {
            CatalogProductResponse response = feignClient.getProductById(productId);

            if (!response.active()) {
                throw new DomainValidationException("El producto " + productId + " no está activo para la venta.");
            }

            return Optional.of(new ProductSnapshot(response.id(), response.name(), response.getPriceAmount()));

        } catch (FeignException.NotFound ex) {
            throw new DomainValidationException("El producto con ID " + productId + " no existe en el catálogo.");
        } catch (FeignException ex) {
            // Falla de red genérica o 500 del microservicio
            throw new RuntimeException("Error de comunicación con Catalog Service", ex);
        }
    }
}
