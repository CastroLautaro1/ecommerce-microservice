package com.ecommerce.user_service.infra.adapters.out.persistence.repository;

import com.ecommerce.user_service.infra.adapters.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByIdAndActiveTrue(Long id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPersonalInfo_DocumentNumberAndIdNot(String documentNumber, Long userId);
    boolean existsByPersonalInfo_PhoneNumberAndIdNot(String phoneNumber, Long userId);
}
