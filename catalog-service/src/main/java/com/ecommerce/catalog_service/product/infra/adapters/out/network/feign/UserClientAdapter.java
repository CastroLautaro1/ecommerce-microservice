package com.ecommerce.catalog_service.product.infra.adapters.out.network.feign;

import com.ecommerce.catalog_service.product.domain.ports.out.SellerValidationPort;
import com.ecommerce.catalog_service.product.infra.adapters.out.network.feign.ports.UserFeignClient;
import com.ecommerce.catalog_service.shared.domain.exception.ExternalServiceIntegrationException;
import feign.FeignException;
import org.springframework.stereotype.Component;

@Component
public class UserClientAdapter implements SellerValidationPort {

    private final UserFeignClient userFeignClient;

    public UserClientAdapter(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public boolean isValidSeller(Long sellerId) {
        try {
            var response = userFeignClient.getSellerStatus(sellerId);

            // Valida que el servicio haya respondido OK y que el payload indique que es válido
            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().isActiveSeller();

        } catch (FeignException.NotFound e) {
            // Si el user-service deuvuelve un 404 retornamos false
            return false;
        } catch (FeignException e) {
            // Excepcion tecnica en caso de caidas de red de parte del user-service
            throw new ExternalServiceIntegrationException("Error de comunicación validando el estado del vendedor", e);
        }
    }
}
