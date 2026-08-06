package com.ecommerce.catalog_service.product.application.services;

import com.ecommerce.catalog_service.product.domain.models.*;
import com.ecommerce.catalog_service.product.application.commands.CreateProductCommand;
import com.ecommerce.catalog_service.product.domain.ports.in.CreateProductUseCase;
import com.ecommerce.catalog_service.product.domain.ports.out.CategoryValidationPort;
import com.ecommerce.catalog_service.product.domain.ports.out.ProductRepositoryPort;
import com.ecommerce.catalog_service.product.domain.ports.out.SellerValidationPort;
import com.ecommerce.catalog_service.shared.domain.exception.BusinessRuleViolationException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort productRepository;
    private final CategoryValidationPort categoryValidation;
    private final SellerValidationPort sellerValidation;

    public CreateProductService(ProductRepositoryPort productRepository, CategoryValidationPort categoryValidation, SellerValidationPort sellerValidation) {
        this.productRepository = productRepository;
        this.categoryValidation = categoryValidation;
        this.sellerValidation = sellerValidation;
    }

    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        validateProductUniqueness(command.name());
        validateCategory(command.categoryId());
        validateSeller(command.sellerId());

        ProductName name = new ProductName(command.name());
        ProductDescription description = new ProductDescription(command.description());
        Price price = new Price(command.priceAmount(), command.priceCurrency());

        Product newProduct = Product.registerProduct(
                name,
                description,
                price,
                command.sellerId(),
                command.categoryId()
        );

        processImages(newProduct, command.imageUrls(), command.sellerId());

        return productRepository.save(newProduct);
    }

    // Crear metodo para validar unicidad del nombre
    private void validateProductUniqueness(String rawName) {
    }

    private void validateCategory(Long categoryId) {
        if (!categoryValidation.existsAndIsActive(categoryId)) {
            throw new BusinessRuleViolationException("La categoría especificada no existe o se encuentra inactiva.");
        }
    }

    private void validateSeller(Long sellerId) {
        if (!sellerValidation.isValidSeller(sellerId)) {
            throw new BusinessRuleViolationException("El identificador del vendedor no es válido o carece de permisos.");
        }
    }

    private void processImages(Product product, List<String> imageUrls, Long sellerId) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        boolean isFirst = true;
        for (String url : imageUrls) {
            // La primera imagen se marca como principal (true), el resto como false
            product.addImage(url, isFirst, sellerId);
            isFirst = false;
        }
    }

}
