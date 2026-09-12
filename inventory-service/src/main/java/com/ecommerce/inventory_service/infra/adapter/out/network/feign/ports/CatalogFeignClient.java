package com.ecommerce.inventory_service.infra.adapter.out.network.feign.ports;

import com.ecommerce.inventory_service.infra.adapter.out.network.feign.dto.OwnershipValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "catalog-service")
public interface CatalogFeignClient {

    @GetMapping("/api/v1/products/{id}/ownership")
    OwnershipValidationResponse checkProductOwnership(@PathVariable("id") Long productId, @RequestParam("sellerId") UUID sellerId);

}
