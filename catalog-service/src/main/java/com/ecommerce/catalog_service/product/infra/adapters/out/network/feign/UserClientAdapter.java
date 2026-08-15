package com.ecommerce.catalog_service.product.infra.adapters.out.network.feign;

import com.ecommerce.catalog_service.product.domain.ports.out.SellerValidationPort;
import com.ecommerce.catalog_service.product.infra.adapters.out.network.feign.ports.UserFeignClient;
import com.ecommerce.catalog_service.shared.domain.exception.ExternalServiceIntegrationException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

@Component
public class UserClientAdapter implements SellerValidationPort {

    private final UserFeignClient userFeignClient;

    public UserClientAdapter(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackForSellerValidation")
    public boolean isValidSeller(Long sellerId) {
        try {
            var response = userFeignClient.getSellerStatus(sellerId);

            // Valida que el servicio haya respondido OK y que el payload indique que es válido
            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().isActiveSeller();

        } catch (FeignException.NotFound e) {
            // Si el user-service devuelve un 404 retornamos false
            return false;
        }
    }

    public boolean fallbackForSellerValidation(Long sellerId, Throwable throwable) {
        // Excepción tecnica que se mapea a un HTTP 503
        throw new ExternalServiceIntegrationException(
                "El servicio de validación de usuarios no se encuentra disponible temporalmente. Por favor, intente nuevamente."
        );
    }
}
