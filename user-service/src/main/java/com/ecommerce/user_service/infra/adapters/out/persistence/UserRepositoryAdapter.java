package com.ecommerce.user_service.infra.adapters.out.persistence;

import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.domain.ports.out.UserRepositoryPort;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.UserJpaEntity;
import com.ecommerce.user_service.infra.adapters.out.persistence.repository.SpringDataUserRepository;

import java.util.Optional;


public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository jpaRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository, UserEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        // 1. Convertir el Dominio puro a Entidad JPA
        UserJpaEntity entityToSave = mapper.toJpaEntity(user);

        // 2. Guardar en PostgreSQL
        UserJpaEntity savedEntity = jpaRepository.save(entityToSave);

        // 3. Devolver el Dominio puro (ahora con el ID generado por la BD)
        return mapper.toDomainModel(savedEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomainModel);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomainModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
}
