package com.ecommerce.order_service.infra.adapters.in.web.exceptions;

import com.ecommerce.order_service.domain.exceptions.DomainValidationException;
import com.ecommerce.order_service.domain.exceptions.ExternalServiceUnavailableException;
import com.ecommerce.order_service.domain.exceptions.InvalidOrderStateException;
import com.ecommerce.order_service.domain.exceptions.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalHandlerException {

    // Validaciones de dominio y reglas de negocio -> 409 Conflict
    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidationException(
            DomainValidationException ex, HttpServletRequest request) {

        log.warn("Violación de regla de negocio en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // Conflicto con el cambio de estado de una Orden -> 409 Conflict
    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderStateException(
            InvalidOrderStateException ex, HttpServletRequest request) {

        log.warn("Transición de estado inválida en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // Recursos no encontados -> 404 Not Found
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFoundException(
            OrderNotFoundException ex, HttpServletRequest request) {

        log.warn("Recurso no encontrado en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // Servicios externos no disponibles -> 503 Service Unavailable
    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleExternalServiceException(
            ExternalServiceUnavailableException ex, HttpServletRequest request
    ) {
        log.warn("Servicio no disponible en {}: {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Fallo interno del servidor en {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // Mensaje ofuscado, no le decimos al front que servicio esta caido
        String secureMessage = "El servicio experimenta una degradación temporal. Por favor, intente nuevamente más tarde.";

        return buildErrorResponse(secureMessage, HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // --- MÉTODO UTILITARIO PARA CONSTRUIR LA RESPUESTA ---
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status, String path) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", path);

        return ResponseEntity.status(status).body(errorResponse);
    }
}
