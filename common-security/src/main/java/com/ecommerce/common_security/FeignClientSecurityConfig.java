package com.ecommerce.common_security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

// Interceptor que solicita un token JWT y lo inyecta en la request
// Sirve para la comunicacion M2M
@Configuration
// BLINDAJE: El interceptor de Feign solo se instanciará si el microservicio
// consumidor definió explícitamente un cliente OAuth2 en su YAML local.
@ConditionalOnBean(ClientRegistrationRepository.class)
public class FeignClientSecurityConfig {

    // Extrae el nombre del microservicio (ej. "order-service") del application.yml local
    @Value("${spring.application.name}")
    private String applicationName;

    // Instancia el motor que sabe cómo ejecutar el flujo "Client Credentials"
    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // Suprime el falso positivo visual del IDE
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials() // Habilita estrictamente M2M
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    // Interceptor de Feign que inyecta el token en la petición saliente
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {

                // Genera el ID del registro dinámicamente: "order-service-client", "inventory-service-client", etc.
                String registrationId = applicationName + "-client";

                // "order-service-client" al igual que la configuracion en el application.yml
                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                        .withClientRegistrationId(registrationId)
                        .principal(applicationName) // Identificador interno representativo
                        .build();

                OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    template.header("Authorization", "Bearer " + authorizedClient.getAccessToken().getTokenValue());
                }
            }
        };
    }

}
