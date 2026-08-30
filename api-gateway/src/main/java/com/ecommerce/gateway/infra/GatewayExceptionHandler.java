package com.ecommerce.gateway.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GatewayExceptionHandler {

    // Intercepta excepciones de estado HTTP de Spring WebFlux (ej. 404 Not Found, 503 Service Unavailable).
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
        return buildErrorResponse(ex.getStatusCode().value(), ex.getReason(), exchange.getRequest().getPath().value());
    }

    // Intercepta cualquier otra excepción no controlada en el enrutamiento (Fallback de seguridad).
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Gateway Error: " + ex.getMessage(), exchange.getRequest().getPath().value());
    }

    // Respuesta estandarizada
    private ResponseEntity<Map<String, Object>> buildErrorResponse(int status, String message, String path) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", LocalDateTime.now().toString());
        errorAttributes.put("status", status);
        errorAttributes.put("message", message);
        errorAttributes.put("path", path);

        return ResponseEntity.status(status).body(errorAttributes);
    }
}
