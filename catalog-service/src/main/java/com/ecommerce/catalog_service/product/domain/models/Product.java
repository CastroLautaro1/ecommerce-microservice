package com.ecommerce.catalog_service.product.domain.models;

import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.shared.domain.exception.ImageLimitExceededException;
import com.ecommerce.catalog_service.shared.domain.exception.UnauthorizedSellerException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Product {

    private Long id;
    private ProductName name;
    private ProductDescription description;
    private Price price; // Uso del Value Object
    private Long sellerId; // Identificador del vendedor (Marketplace)
    private Long categoryId;
    private List<ProductImage> images;
    private boolean active;
    private LocalDateTime createdAt;

    private static final int MAX_IMAGES_ALLOWED = 5;

    // Factory Method para la creacion de un Producto
    public static Product registerProduct(ProductName name, ProductDescription description, Price price, Long sellerId, Long categoryId) {
        if (sellerId == null) throw new BusinessRuleViolationException("El producto debe pertenecer a un vendedor");
        if (categoryId == null) throw new BusinessRuleViolationException("El producto debe tener una categoría asociada");

        return new Product(
                null,
                name,
                description,
                price,
                sellerId,
                categoryId,
                new ArrayList<>(),
                true,
                LocalDateTime.now()
        );
    }

    // Constructor para reconstituir el producto desde la Base de Datos
    public Product(Long id, ProductName name, ProductDescription description, Price price, Long sellerId,
                   Long categoryId, List<ProductImage> images, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.images = images != null ? new ArrayList<>(images) : new ArrayList<>();
        this.active = active;
        this.createdAt = createdAt;
    }

    // --- COMPORTAMIENTO DEL DOMINIO (Reglas de Negocio) ---

    public void updatePrice(Price newPrice, Long requestingUserId) {
        verifyOwnership(requestingUserId);
        this.price = newPrice;
    }

    public void deactivate(Long requestingUserId) {
        verifyOwnership(requestingUserId);
        this.active = false;
    }

    public void updateDetails(ProductName newName, ProductDescription newDescription, Price newPrice, Long newCategoryId, Long requestingUserId) {
        verifyOwnership(requestingUserId);
        if (newCategoryId == null) throw new BusinessRuleViolationException("El producto debe tener una categoría asociada");
        if (newPrice == null) throw new BusinessRuleViolationException("El precio es obligatorio");

        this.name = newName;
        this.description = newDescription;
        this.price = newPrice;
        this.categoryId = newCategoryId;
    }

    public void replaceImages(List<String> newImageUrls, Long requestingUserId) {
        verifyOwnership(requestingUserId);

        // Vaciamos la galería actual
        this.images.clear();

        // Si mandaron nuevas imágenes, las procesamos usando nuestra regla existente
        if (newImageUrls != null && !newImageUrls.isEmpty()) {
            boolean isFirst = true;
            for (String url : newImageUrls) {
                this.addImage(url, isFirst, requestingUserId); // Validará el límite de 5 automáticamente
                isFirst = false;
            }
        }
    }

    /**
     * Regla de Negocio Compleja: Agregar una imagen.
     * Si la nueva es principal, las demás dejan de serlo.
     */
    public void addImage(String url, boolean isMain, Long requestingUserId) {
        verifyOwnership(requestingUserId);

        if (this.images.size() >= MAX_IMAGES_ALLOWED) {
            throw new ImageLimitExceededException(MAX_IMAGES_ALLOWED);
        }

        // Si la nueva es principal, actualizamos el resto (invariante de negocio)
        if (isMain) {
            List<ProductImage> updatedImages = new ArrayList<>();
            for (ProductImage img : this.images) {
                updatedImages.add(img.asNonMain()); // Convertimos las existentes a no-principales
            }
            this.images = updatedImages;
        } else if (this.images.isEmpty()) {
            // Si es la primera imagen que se sube, forzamos a que sea la principal
            isMain = true;
        }

        this.images.add(new ProductImage(url, isMain));
    }

    public void removeImage(String url, Long requestingUserId) {
        verifyOwnership(requestingUserId);
        this.images.removeIf(img -> img.url().equals(url));

        // Si borramos la principal y quedan imágenes, asignamos la primera como principal
        boolean hasMain = this.images.stream().anyMatch(ProductImage::isMain);
        if (!hasMain && !this.images.isEmpty()) {
            ProductImage first = this.images.get(0);
            this.images.set(0, new ProductImage(first.url(), true));
        }
    }

    // --- MÉTODOS DE PROTECCIÓN INTERNA ---

    private void verifyOwnership(Long userId) {
        if (!this.sellerId.equals(userId)) {
            // Regla de Marketplace: Solo el dueño puede mutar el producto
            throw new UnauthorizedSellerException(userId, this.id);
        }
    }

    // --- GETTERS (Solo para lectura en la capa de persistencia/presentación) ---

    public Long getId() { return id; }
    public String getName() { return name.value(); }
    public String getDescription() { return description.value(); }
    public Price getPrice() { return price; }
    public Long getSellerId() { return sellerId; }
    public Long getCategoryId() { return categoryId; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<ProductImage> getImages() {
        return Collections.unmodifiableList(images); // Lista inmodificable
    }
}
