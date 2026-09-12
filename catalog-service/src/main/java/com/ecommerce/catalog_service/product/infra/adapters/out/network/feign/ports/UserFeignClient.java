package com.ecommerce.catalog_service.product.infra.adapters.out.network.feign.ports;

import com.ecommerce.catalog_service.product.infra.adapters.out.network.feign.dto.SellerStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{userId}/user-status")
    ResponseEntity<SellerStatusResponse> getSellerStatus(@PathVariable("userId") UUID userId);

}
