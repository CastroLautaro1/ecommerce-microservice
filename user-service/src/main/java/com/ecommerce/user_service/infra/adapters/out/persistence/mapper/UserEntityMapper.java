package com.ecommerce.user_service.infra.adapters.out.persistence.mapper;

import com.ecommerce.user_service.domain.models.Address;
import com.ecommerce.user_service.domain.models.PersonalInfo;
import com.ecommerce.user_service.domain.models.SavedPaymentMethod;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.AddressJpaEntity;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.PersonalInfoEmbeddable;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.SavedPaymentMethodJpaEntity;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.UserJpaEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        // FUNDAMENTAL: Obliga a MapStruct a usar entity.addAddress() y entity.addPaymentMethod()
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface UserEntityMapper {

    // ==========================================
    // 1. Dominio -> JPA (Para Guardar)
    // ==========================================
    UserJpaEntity toJpaEntity(User user);

    PersonalInfoEmbeddable toJpaPersonalInfo(PersonalInfo domainInfo);

    @Mapping(target = "user", ignore = true) // Previene ciclos infinitos si existiera el setter
    AddressJpaEntity toJpaAddress(Address domainAddress);

    @Mapping(target = "user", ignore = true)
    SavedPaymentMethodJpaEntity toJpaPaymentMethod(SavedPaymentMethod domainPayment);


    // ==========================================
    // 2. JPA -> Dominio (Para Leer)
    // ==========================================

    // Interceptamos específicamente el mapeo de PersonalInfo para aplicar tu regla de negocio
    @Mapping(source = "personalInfo", target = "personalInfo", qualifiedByName = "toDomainPersonalInfoSafe")
    User toDomainModel(UserJpaEntity entity);

    Address toDomainAddress(AddressJpaEntity entity);

    SavedPaymentMethod toDomainPaymentMethod(SavedPaymentMethodJpaEntity entity);

    // ==========================================
    // 3. Métodos Auxiliares y Reglas de Negocio
    // ==========================================

    @Named("toDomainPersonalInfoSafe")
    default PersonalInfo toDomainPersonalInfoSafe(PersonalInfoEmbeddable entityInfo) {
        if (entityInfo == null || entityInfo.getFirstName() == null) {
            return null; // Regla de dominio: Perfil incompleto
        }

        return new PersonalInfo(
                entityInfo.getFirstName(),
                entityInfo.getLastName(),
                entityInfo.getDocumentNumber(),
                entityInfo.getPhoneNumber()
        );
    }
}
