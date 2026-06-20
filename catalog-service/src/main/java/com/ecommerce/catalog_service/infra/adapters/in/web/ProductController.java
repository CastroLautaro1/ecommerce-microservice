package com.ecommerce.catalog_service.infra.adapters.in.web;

import com.ecommerce.catalog_service.domain.ports.in.*;
import com.ecommerce.catalog_service.infra.adapters.in.web.dto.CreateProductRequest;
import com.ecommerce.catalog_service.infra.adapters.in.web.dto.UpdateProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
        CreateProductCommand command = new CreateProductCommand(
                request.name(), request.description(), request.priceAmount(),
                request.priceCurrency(), request.sellerId(), request.categoryId(),
                request.imageUrls()
        );
        var product = createProductUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody UpdateProductRequest request) {
        UpdateProductCommand command = new UpdateProductCommand(
                id, request.requestingUserId(), request.name(), request.description(),
                request.priceAmount(), request.priceCurrency(), request.categoryId(),
                request.imageUrls()
        );
        var product = updateProductUseCase.execute(command);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id, @RequestParam Long requestingUserId) {
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
}
