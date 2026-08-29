package com.ecommerce.gateway.infra;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Sin CSRF porque la arquitectura es Stateless
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Acceso público para las peticiones GET del catálogo
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        // Cualquier otra petición requiere JWT
                        .anyExchange().authenticated()
                )
                // Habilitamos el Resource Server para que valide el JWT usando las claves públicas de Keycloak
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()))
                .build();
    }
}
