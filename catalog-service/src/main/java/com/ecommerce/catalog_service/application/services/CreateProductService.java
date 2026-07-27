package com.ecommerce.catalog_service.application.services;

import com.ecommerce.catalog_service.domain.models.Price;
import com.ecommerce.catalog_service.domain.models.Product;
import com.ecommerce.catalog_service.application.commands.CreateProductCommand;
import com.ecommerce.catalog_service.domain.ports.in.CreateProductUseCase;
import com.ecommerce.catalog_service.domain.ports.out.ProductRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateProductService implements CreateProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public CreateProductService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    @Transactional
    public Product execute(CreateProductCommand command) {
        Price price = new Price(command.priceAmount(), command.priceCurrency());

        Product newProduct = Product.registerProduct(
                command.name(),
                command.description(),
                price,
                command.sellerId(),
                command.categoryId()
        );

        if (command.imageUrls() != null && !command.imageUrls().isEmpty()) {
            boolean isFirst = true;
            for (String url : command.imageUrls()) {
                // La primera imagen se marca como principal (true), el resto como false
                newProduct.addImage(url, isFirst, command.sellerId());
                isFirst = false;
            }
        }

        return productRepositoryPort.save(newProduct);
    }
}
