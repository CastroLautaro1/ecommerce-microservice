package com.ecommerce.catalog_service.application;

import com.ecommerce.catalog_service.domain.models.Product;
import com.ecommerce.catalog_service.domain.ports.in.DeactivateProductCommand;
import com.ecommerce.catalog_service.domain.ports.in.DeactivateProductUseCase;
import com.ecommerce.catalog_service.domain.ports.out.ProductRepositoryPort;
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
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        product.deactivate(command.requestingUserId()); // El dominio se encarga de la logica

        repository.save(product);
    }
}
