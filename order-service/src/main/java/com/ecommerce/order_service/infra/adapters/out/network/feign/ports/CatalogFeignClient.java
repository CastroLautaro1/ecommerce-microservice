package com.ecommerce.order_service.infra.adapters.out.network.feign.ports;

import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.CatalogProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", url = "${application.services.catalog.url}")
public interface CatalogFeignClient {
    @GetMapping("/api/v1/products/{productId}")
    CatalogProductResponse getProductById(@PathVariable("productId") Long productId);
}
