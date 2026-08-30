package com.ecommerce.catalog_service.product.application.services;

import com.ecommerce.catalog_service.product.domain.models.Price;
import com.ecommerce.catalog_service.product.domain.models.Product;
import com.ecommerce.catalog_service.product.application.commands.UpdateProductCommand;
import com.ecommerce.catalog_service.product.domain.models.ProductDescription;
import com.ecommerce.catalog_service.product.domain.models.ProductName;
import com.ecommerce.catalog_service.product.domain.ports.in.UpdateProductUseCase;
import com.ecommerce.catalog_service.product.domain.ports.out.CategoryValidationPort;
import com.ecommerce.catalog_service.product.domain.ports.out.ProductRepositoryPort;
import com.ecommerce.catalog_service.product.domain.ports.out.SellerValidationPort;
import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateProductService implements UpdateProductUseCase {

    private final ProductRepositoryPort repository;
    private final CategoryValidationPort categoryValidation;
    private final SellerValidationPort sellerValidation;

    public UpdateProductService(ProductRepositoryPort repository, CategoryValidationPort categoryValidation, SellerValidationPort sellerValidation) {
        this.repository = repository;
        this.categoryValidation = categoryValidation;
        this.sellerValidation = sellerValidation;
    }

    @Override
    @Transactional
    public Product execute(UpdateProductCommand command) {
        validateCategory(command.categoryId());
        validateSeller(command.requestingUserId());

        Product product = repository.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", command.productId()));

        validateProductUniquenessOnUpdate(command.name(), command.productId());

        ProductName newName = new ProductName(command.name());
        ProductDescription newDescription = new ProductDescription(command.description());
        Price newPrice = new Price(command.priceAmount(), command.priceCurrency());

        product.updateDetails(
                newName,
                newDescription,
                newPrice,
                command.categoryId(),
                command.requestingUserId()
        );

        product.replaceImages(command.imageUrls(), command.requestingUserId());

        return repository.save(product);
    }

    private void validateProductUniquenessOnUpdate(String rawName, Long currentProductId) {
        // Verifica si existe OTRO producto con el mismo nombre exacto
        if (repository.existsByNameAndIdNot(rawName.trim(), currentProductId)) {
            throw new BusinessRuleViolationException(
                    "Ya existe otro producto registrado con el nombre especificado."
            );
        }
    }

    private void validateCategory(Long categoryId) {
        if (!categoryValidation.existsAndIsActive(categoryId)) {
            throw new BusinessRuleViolationException("La categoría especificada no existe o se encuentra inactiva.");
        }
    }

    private void validateSeller(UUID sellerId) {
        if (!sellerValidation.isValidSeller(sellerId)) {
            throw new BusinessRuleViolationException("El identificador del vendedor no es válido o carece de permisos.");
        }
    }
}
