package com.ecommerce.user_service.infra.adapters.out.persistence.repository;

import com.ecommerce.user_service.infra.adapters.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByIdAndActiveTrue(UUID id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPersonalInfo_DocumentNumberAndIdNot(String documentNumber, UUID userId);
    boolean existsByPersonalInfo_PhoneNumberAndIdNot(String phoneNumber, UUID userId);

    @Query("SELECT u.active FROM UserJpaEntity u WHERE u.id = :id")
    Optional<Boolean> findActiveStatusById(@Param("id") UUID id);

    boolean existsByIdAndActiveTrue(UUID userId);
}
