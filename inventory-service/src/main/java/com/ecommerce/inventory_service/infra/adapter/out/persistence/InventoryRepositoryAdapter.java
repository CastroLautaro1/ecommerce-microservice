package com.ecommerce.inventory_service.infra.adapter.out.persistence;

import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.InventoryJpaEntity;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.mapper.InventoryMapper;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.repository.SpringDataInventoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final SpringDataInventoryRepository springDataRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryRepositoryAdapter(SpringDataInventoryRepository springDataRepository, InventoryMapper inventoryMapper) {
        this.springDataRepository = springDataRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public Inventory save(Inventory inventory) {
        InventoryJpaEntity jpaEntity = inventoryMapper.toJpaEntity(inventory);
        InventoryJpaEntity savedEntity = springDataRepository.save(jpaEntity);
        return inventoryMapper.toDomain(savedEntity);
    }

    @Override
    public void saveAll(List<Inventory> inventories) {
        List<InventoryJpaEntity> entities = inventories.stream()
                .map(inventoryMapper::toJpaEntity)
                .toList();

        springDataRepository.saveAll(entities);
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return springDataRepository.findById(id)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        return springDataRepository.findByProductId(productId)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Optional<Inventory> findBySku(String sku) {
        return springDataRepository.findBySku(sku)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Optional<Inventory> findByReservationId(UUID reservationId) {
        return springDataRepository.findByReservationId(reservationId)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public Optional<Inventory> findByProductIdWithLock(Long productId) {
        // Ejecuta la query nativa con el bloqueo pesimista
        return springDataRepository.findByProductIdWithLock(productId)
                .map(inventoryMapper::toDomain);
    }
}
