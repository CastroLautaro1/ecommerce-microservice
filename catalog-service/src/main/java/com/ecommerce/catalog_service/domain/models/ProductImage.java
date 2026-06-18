package com.ecommerce.catalog_service.domain.models;

public record ProductImage(String url, boolean isMain) {
    public ProductImage {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("La URL de la imagen es obligatoria");
        }
        if (!url.startsWith("http")) {
            throw new IllegalArgumentException("La URL de la imagen debe ser un enlace válido");
        }
    }

    /**
     * Al ser inmutable, si necesitamos cambiar esta imagen a "no principal",
     * devolvemos una nueva instancia con los mismos datos pero con isMain en false.
     */
    public ProductImage asNonMain() {
        return new ProductImage(this.url, false);
    }
}
