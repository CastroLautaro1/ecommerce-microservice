package com.ecommerce.catalog_service.product.application.services;

import com.ecommerce.catalog_service.product.domain.models.Product;
import com.ecommerce.catalog_service.product.application.commands.DeactivateProductCommand;
import com.ecommerce.catalog_service.product.domain.ports.in.DeactivateProductUseCase;
import com.ecommerce.catalog_service.product.domain.ports.out.ProductRepositoryPort;
import com.ecommerce.catalog_service.shared.domain.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeactivateProductService implements DeactivateProductUseCase {

    private final ProductRepositoryPort repository;

    public DeactivateProductService(ProductRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void execute(DeactivateProductCommand command) {
        Product product = repository.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", command.productId()));

        product.deactivate(command.requestingUserId()); // El dominio se encarga de la logica

        repository.save(product);
    }
}
