package com.ecommerce.catalog_service.application;

import com.ecommerce.catalog_service.domain.models.Price;
import com.ecommerce.catalog_service.domain.models.Product;
import com.ecommerce.catalog_service.domain.ports.in.UpdateProductCommand;
import com.ecommerce.catalog_service.domain.ports.in.UpdateProductUseCase;
import com.ecommerce.catalog_service.domain.ports.out.ProductRepositoryPort;
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
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Price newPrice = new Price(command.priceAmount(), command.priceCurrency());

        product.updateDetails(
                command.name(),
                command.description(),
                newPrice,
                command.categoryId(),
                command.requestingUserId()
        );

        product.replaceImages(command.imageUrls(), command.requestingUserId());

        return repository.save(product);
    }
}
