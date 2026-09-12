package com.ecommerce.inventory_service.infra.adapter.out.network.feign;

import com.ecommerce.inventory_service.domain.exceptions.ExternalServiceUnavailableException;
import com.ecommerce.inventory_service.domain.ports.out.ProductValidationPort;
import com.ecommerce.inventory_service.infra.adapter.out.network.feign.dto.OwnershipValidationResponse;
import com.ecommerce.inventory_service.infra.adapter.out.network.feign.ports.CatalogFeignClient;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CatalogClientAdapter implements ProductValidationPort {

    private final CatalogFeignClient feignClient;

    public CatalogClientAdapter(CatalogFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    public boolean isProductOwnedBySeller(Long productId, UUID sellerId) {
        try {
            OwnershipValidationResponse response = feignClient.checkProductOwnership(productId, sellerId);
            return response.isValid();
        } catch (FeignException.NotFound e) {
            // El catálogo confirma que el producto NO existe.
            return false;
        } catch (FeignException e) {
            // Fallas de red, Timeouts, 500s del Catálogo, o fallas del token M2M (401).
            throw new ExternalServiceUnavailableException(
                    "Falla de comunicación con catalog-service al validar propiedad del producto ID: " + productId, e
            );
        }
    }
}
