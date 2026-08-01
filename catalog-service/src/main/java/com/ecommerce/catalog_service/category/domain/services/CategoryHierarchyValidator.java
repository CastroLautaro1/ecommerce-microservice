package com.ecommerce.catalog_service.category.domain.services;

import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.out.CategoryRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class CategoryHierarchyValidator {
    private final CategoryRepositoryPort categoryRepository;

    public CategoryHierarchyValidator(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Valida que al asignar un nuevo padre a una categoría, no se generen ciclos jerárquicos.
    public void validateNoCycles(Long categoryIdToUpdate, Long newParentId) {
        if (newParentId == null) {
            return; // Convertir a raíz nunca genera ciclos
        }

        Long currentAncestorId = newParentId;

        // Escalar el árbol hacia la raíz
        while (currentAncestorId != null) {
            if (currentAncestorId.equals(categoryIdToUpdate)) {
                throw new BusinessRuleViolationException(
                        "Regla de jerarquía violada: No se puede asignar como padre a un nodo descendiente (Ciclo detectado)."
                );
            }

            // Rehidratamos el ancestro para obtener a su vez a su padre.
            Category ancestor = categoryRepository.findById(currentAncestorId)
                    .orElseThrow(() -> new BusinessRuleViolationException("El ancestro en la validación de ciclos no existe."));

            currentAncestorId = ancestor.getParentId();
        }
    }
}
