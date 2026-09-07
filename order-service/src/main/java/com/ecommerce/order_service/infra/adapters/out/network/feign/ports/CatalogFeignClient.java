package com.ecommerce.order_service.infra.adapters.out.network.feign.ports;

import com.ecommerce.common_security.FeignClientSecurityConfig;
import com.ecommerce.order_service.infra.adapters.out.network.feign.dto.CatalogProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "catalog-service",
        url = "${application.services.catalog.url}",
        configuration = FeignClientSecurityConfig.class
)
public interface CatalogFeignClient {
    @GetMapping("/api/v1/products/{productId}")
    CatalogProductResponse getProductById(@PathVariable("productId") Long productId);
}
