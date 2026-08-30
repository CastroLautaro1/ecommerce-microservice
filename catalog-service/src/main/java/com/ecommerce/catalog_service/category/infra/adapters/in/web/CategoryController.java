package com.ecommerce.catalog_service.category.infra.adapters.in.web;

import com.ecommerce.catalog_service.category.application.commands.CreateCategoryCommand;
import com.ecommerce.catalog_service.category.application.commands.MoveCategoryCommand;
import com.ecommerce.catalog_service.category.application.commands.PromoteCategoryToRootCommand;
import com.ecommerce.catalog_service.category.application.commands.RenameCategoryCommand;
import com.ecommerce.catalog_service.category.domain.models.Category;
import com.ecommerce.catalog_service.category.domain.ports.in.*;
import com.ecommerce.catalog_service.category.infra.adapters.in.web.dto.CreateCategoryRequest;
import com.ecommerce.catalog_service.category.infra.adapters.in.web.dto.MoveCategoryRequest;
import com.ecommerce.catalog_service.category.infra.adapters.in.web.dto.RenameCategoryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategory;
    private final DeactivateCategoryUseCase deactivateCategory;
    private final RenameCategoryUseCase renameCategory;
    private final MoveCategoryUseCase moveCategory;
    private final PromoteCategoryToRootUseCase promoteCategoryToRoot;

    public CategoryController(CreateCategoryUseCase createCategory, DeactivateCategoryUseCase deactivateCategory, RenameCategoryUseCase renameCategory, MoveCategoryUseCase moveCategory, PromoteCategoryToRootUseCase promoteCategoryToRoot) {
        this.createCategory = createCategory;
        this.deactivateCategory = deactivateCategory;
        this.renameCategory = renameCategory;
        this.moveCategory = moveCategory;
        this.promoteCategoryToRoot = promoteCategoryToRoot;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
        Category category = createCategory.execute(new CreateCategoryCommand(request.name(), request.parentId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    // Renombrar categoria
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/name")
    public ResponseEntity<Void> rename(@PathVariable Long id, @RequestBody RenameCategoryRequest request) {
        renameCategory.execute(new RenameCategoryCommand(id, request.newName()));
        return ResponseEntity.noContent().build();
    }

    // Mover de jerarquía
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/parent")
    public ResponseEntity<Void> move(@PathVariable Long id, @RequestBody MoveCategoryRequest request) {
        moveCategory.execute(new MoveCategoryCommand(id, request.parentId()));
        return ResponseEntity.noContent().build();
    }

    // Promover a raíz (semanticamente se "elimina" la relacion con el padre)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/parent")
    public ResponseEntity<Void> promoteToRoot(@PathVariable Long id) {
        promoteCategoryToRoot.execute(new PromoteCategoryToRootCommand(id));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        deactivateCategory.execute(id);
        return ResponseEntity.noContent().build();
    }


}
