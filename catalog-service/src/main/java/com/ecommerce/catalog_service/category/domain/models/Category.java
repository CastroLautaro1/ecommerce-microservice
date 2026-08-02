package com.ecommerce.catalog_service.category.domain.models;

import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;

public class Category {

    private Long id;
    private String name;
    private Long parentId; // Clave para formar el árbol. Si es null, es una Categoría Raíz.
    private boolean active;

    // Factory Method para crear una nueva Categoria
    public static Category registerCategory(String name, Long parentId) {
        validateName(name);

        return new Category(
                null,
                name,
                parentId, // Puede ser null
                true
        );
    }

    // Constructor para reconstituir desde la Base de Datos
    public Category(Long id, String name, Long parentId, boolean active) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.active = active;
    }

    // --- COMPORTAMIENTO DEL DOMINIO ---

    // Actualiza el nombre de la categoria
    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    // Actualiza la Cateogria padre actual
    public void changeParent(Long newParentId) {
        // El caso de uso se encarga de ejecutar la validacion de ciclo profundo
        this.parentId = newParentId;
    }

    // Convierte esta categoría en un nodo raíz (sin padre).
    public void promoteToRoot() {
        this.parentId = null;
    }

    public void deactivate() {
        if (!this.active) {
            throw new BusinessRuleViolationException("La categoría ya se encuentra inactiva.");
        }
        this.active = false;
    }

    // --- MÉTODOS DE PROTECCIÓN INTERNA ---

    private static void validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre de la categoría debe tener al menos 3 caracteres");
        }
    }

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getParentId() { return parentId; }
    public boolean isRoot() { return parentId == null; }
    public boolean isActive() { return active; }
}
