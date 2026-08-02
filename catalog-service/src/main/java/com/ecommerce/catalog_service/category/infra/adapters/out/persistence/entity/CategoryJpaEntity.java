package com.ecommerce.catalog_service.category.infra.adapters.out.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Objects;

@Entity
@Table(name = "categories")
@DynamicUpdate
public class CategoryJpaEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Patrón Adjacency List: Se mapea como un Long puro en lugar de un @ManyToOne.
     * Esto evita cargas recursivas accidentales (N+1) por parte de Hibernate
     * al mapear la entidad hacia el Dominio.
     */
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "active", nullable = false)
    private boolean active;

    // Constructores requeridos por JPA
    public CategoryJpaEntity() {}

    public CategoryJpaEntity(Long id, String name, Long parentId, boolean active) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.active = active;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // equals() y hashCode() basados estrictamente en la clave primaria (id)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryJpaEntity that = (CategoryJpaEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
