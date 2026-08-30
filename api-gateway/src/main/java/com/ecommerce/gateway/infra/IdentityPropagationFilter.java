package com.ecommerce.gateway.infra;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class IdentityPropagationFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Extraemos el contexto de seguridad reactivo
        return ReactiveSecurityContextHolder.getContext()
                // Se verifica la existencia de una autenticacion de tipo JWT
                .filter(context -> context.getAuthentication() != null &&
                        context.getAuthentication().getPrincipal() instanceof Jwt)
                .map(context -> (Jwt) context.getAuthentication().getPrincipal())
                .map(jwt -> {
                    // Extraer el claim 'sub', o sea el ID del usuario
                    String userId = jwt.getClaimAsString("sub");

                    // Se muta la peticion para agregar la nueva cabecera
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header(USER_ID_HEADER, userId)
                            .build();

                    // Mutamos el intercambio con la nueva petición
                    return exchange.mutate().request(mutatedRequest).build();
                })
                // Si el usuario es anónimo (no hay JWT), pasamos la petición original intacta
                .defaultIfEmpty(exchange)
                // Continuamos la ejecución hacia el microservicio de destino
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        // Prioridad 0: Se ejecuta justo después de la capa de seguridad (-100)
        // y antes del enrutamiento de red de Gateway
        return 0;
    }
}
