package com.ecommerce.inventory_service.infra.adapter.in.web.exceptions;

import com.ecommerce.inventory_service.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Recursos no encontrados -> 404 Not Found
    @ExceptionHandler({
            InventoryNotFoundException.class,
            ReservationNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(
            RuntimeException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // Conflictos de estado y reglas de negocio criticas -> 409 Conflict
    // (Se agrupan la falta de stock y los intentos de modificar reservas ya confirmadas/expiradas)
    @ExceptionHandler({
            InsufficientStockException.class,
            InvalidReservationStateException.class,
            InvalidProductOwnershipException.class,
            BusinessRuleViolationException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflictExceptions(
            RuntimeException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // Validaciones de dominio y sintaxis -> 400 Bad Request
    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidationException(
            DomainValidationException ex,
            HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // JSON malformados -> 400 Bad Request
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return buildErrorResponse("El cuerpo de la petición (JSON) contiene un formato inválido",
                HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    // Fallas de Red y Dependencias Externas -> 503 Service Unavailable
    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleExternalServiceFailures(
            ExternalServiceUnavailableException ex,
            HttpServletRequest request) {
        ex.printStackTrace();

        return buildErrorResponse("\"No se pudo validar la transacción debido a una falla temporal en los servicios internos.",
                HttpStatus.SERVICE_UNAVAILABLE, request.getRequestURI());
    }

    // Errores inesperados -> 500 Internal Server Error
    @ExceptionHandler({
            Exception.class,
            ExternalServiceUnavailableException.class
    })
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        ex.printStackTrace();

        return buildErrorResponse("Ocurrió un error interno en el servidor",
                HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
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
