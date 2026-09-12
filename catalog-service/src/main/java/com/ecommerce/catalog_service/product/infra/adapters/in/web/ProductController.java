package com.ecommerce.catalog_service.product.infra.adapters.in.web;

import com.ecommerce.catalog_service.product.application.commands.CreateProductCommand;
import com.ecommerce.catalog_service.product.application.commands.DeactivateProductCommand;
import com.ecommerce.catalog_service.product.application.commands.UpdateProductCommand;
import com.ecommerce.catalog_service.product.infra.adapters.in.web.dto.CreateProductRequest;
import com.ecommerce.catalog_service.product.infra.adapters.in.web.dto.OwnershipValidationResponse;
import com.ecommerce.catalog_service.product.infra.adapters.in.web.dto.UpdateProductRequest;
import com.ecommerce.catalog_service.product.domain.ports.in.CreateProductUseCase;
import com.ecommerce.catalog_service.product.domain.ports.in.DeactivateProductUseCase;
import com.ecommerce.catalog_service.product.domain.ports.in.ProductQueryUseCase;
import com.ecommerce.catalog_service.product.domain.ports.in.UpdateProductUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final ProductQueryUseCase productQueryUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeactivateProductUseCase deactivateProductUseCase,
            ProductQueryUseCase productQueryUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deactivateProductUseCase = deactivateProductUseCase;
        this.productQueryUseCase = productQueryUseCase;
    }

    // --- COMANDOS (Mutaciones) ---

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createProduct(
            @RequestBody CreateProductRequest request,
            @RequestHeader("X-User-Id") UUID requestingUserId
    ) {
        CreateProductCommand command = new CreateProductCommand(
                request.name(), request.description(), request.priceAmount(),
                request.priceCurrency(), requestingUserId, request.categoryId(),
                request.imageUrls()
        );
        var product = createProductUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody UpdateProductRequest request,
            @RequestHeader("X-User-Id") UUID requestingUserId
    ) {
        UpdateProductCommand command = new UpdateProductCommand(
                id, requestingUserId, request.name(), request.description(),
                request.priceAmount(), request.priceCurrency(), request.categoryId(),
                request.imageUrls()
        );
        var product = updateProductUseCase.execute(command);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") UUID requestingUserId
    ) {
        DeactivateProductCommand command = new DeactivateProductCommand(id, requestingUserId);
        deactivateProductUseCase.execute(command);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    // --- CONSULTAS (Lecturas) ---

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productQueryUseCase.getProductDetails(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllActiveProducts() {
        return ResponseEntity.ok(productQueryUseCase.getAllActiveProducts());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productQueryUseCase.searchByName(keyword));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productQueryUseCase.filterByCategory(categoryId));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(productQueryUseCase.filterByPriceRange(minPrice, maxPrice));
    }

    // Endpoint para que el Inventory-Service pueda validar que el producto existe y le pertence al vendedor
    @GetMapping("/{id}/ownership")
    public ResponseEntity<OwnershipValidationResponse> isProductOwnedBySeller(
            @PathVariable("id") Long productId,
            @RequestParam("sellerId") UUID sellerId
    ) {
        boolean isValid = productQueryUseCase.checkProductOwnership(productId, sellerId);
        return ResponseEntity.ok(new OwnershipValidationResponse(isValid));
    }
}
