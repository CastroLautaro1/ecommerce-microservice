package com.ecommerce.catalog_service.shared.domain.exception;

public class ImageLimitExceededException extends RuntimeException {
    public ImageLimitExceededException(int maxImages) {
        super("No se pueden subir más de " + maxImages + " imágenes por producto.");
    }
}
