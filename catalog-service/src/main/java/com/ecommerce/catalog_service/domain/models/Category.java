package com.ecommerce.catalog_service.domain.models;

public class Category {

    private Long id;
    private String name;
    private Long parentId; // Clave para formar el árbol. Si es null, es una Categoría Raíz.
    private boolean active;

    // Constructor para una NUEVA categoría
    public Category(String name, Long parentId) {
        validateName(name);
        this.name = name;
        this.parentId = parentId; // Puede ser null
        this.active = true;
    }

    // Constructor para reconstituir desde la Base de Datos
    public Category(Long id, String name, Long parentId, boolean active) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.active = active;
    }

    // --- COMPORTAMIENTO DEL DOMINIO ---

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName;
    }

    public void moveUnderParent(Long newParentId) {
        // Regla de negocio: Una categoría no puede ser padre de sí misma
        if (this.id != null && this.id.equals(newParentId)) {
            throw new IllegalStateException("Una categoría no puede ser su propio padre");
        }
        this.parentId = newParentId;
    }

    public void makeRoot() {
        this.parentId = null;
    }

    public void deactivate() {
        this.active = false;
    }

    // --- MÉTODOS DE PROTECCIÓN INTERNA ---

    private void validateName(String name) {
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
