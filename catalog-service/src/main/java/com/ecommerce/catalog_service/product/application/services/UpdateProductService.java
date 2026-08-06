package com.ecommerce.catalog_service.product.application.services;

import com.ecommerce.catalog_service.product.domain.models.Price;
import com.ecommerce.catalog_service.product.domain.models.Product;
import com.ecommerce.catalog_service.product.application.commands.UpdateProductCommand;
import com.ecommerce.catalog_service.product.domain.models.ProductDescription;
import com.ecommerce.catalog_service.product.domain.models.ProductName;
import com.ecommerce.catalog_service.product.domain.ports.in.UpdateProductUseCase;
import com.ecommerce.catalog_service.product.domain.ports.out.ProductRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductService implements UpdateProductUseCase {

    private final ProductRepositoryPort repository;

    public UpdateProductService(ProductRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Product execute(UpdateProductCommand command) {
        Product product = repository.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", command.productId()));

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
}
