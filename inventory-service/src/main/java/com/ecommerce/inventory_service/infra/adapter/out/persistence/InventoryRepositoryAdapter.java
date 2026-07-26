package com.ecommerce.inventory_service.infra.adapter.out.persistence;

import com.ecommerce.inventory_service.domain.models.Inventory;
import com.ecommerce.inventory_service.domain.ports.out.InventoryRepositoryPort;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.entity.InventoryJpaEntity;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.mapper.InventoryMapper;
import com.ecommerce.inventory_service.infra.adapter.out.persistence.repository.SpringDataInventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

//    @Override
//    public void saveAll(List<Inventory> inventories) {
//        List<InventoryJpaEntity> entities = inventories.stream()
//                .map(inventoryMapper::toJpaEntity)
//                .toList();
//
//        springDataRepository.saveAll(entities);
//    }

    @Override
    @Transactional
    public void saveAll(List<Inventory> inventoriesDomain) {
        // Se extraen los IDs para buscar en bloque
        List<Long> inventoryIds = inventoriesDomain.stream()
                .map(Inventory::getId)
                .toList();

        // Se recuperan las entidades existentes
        Map<Long, InventoryJpaEntity> existingEntities = springDataRepository.findAllById(inventoryIds)
                .stream()
                .collect(Collectors.toMap(InventoryJpaEntity::getId, Function.identity()));

        for (Inventory domain : inventoriesDomain) {
            InventoryJpaEntity jpaEntity = existingEntities.get(domain.getId());

            if (jpaEntity != null) {
                // Flujo de actualizacion
                inventoryMapper.updateEntityFromDomain(jpaEntity, domain);
            } else {
                // Flujo de creacion
                InventoryJpaEntity newEntity = inventoryMapper.toJpaEntity(domain);
                springDataRepository.save(newEntity);
            }
        }
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return springDataRepository.findById(id)
                .map(inventoryMapper::toDomain);
    }

    @Override
    public List<Inventory> findInventoriesWithReservationsByOrderId(UUID orderId) {
        return springDataRepository.findByReservationsOrderId(orderId).stream()
                .map(inventoryMapper::toDomain)
                .collect(Collectors.toList());
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
