package com.ecommerce.user_service.infra.adapters.out.persistence;

import com.ecommerce.user_service.domain.models.Address;
import com.ecommerce.user_service.domain.models.PersonalInfo;
import com.ecommerce.user_service.domain.models.SavedPaymentMethod;
import com.ecommerce.user_service.domain.models.User;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.AddressJpaEntity;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.PersonalInfoEmbeddable;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.SavedPaymentMethodJpaEntity;
import com.ecommerce.user_service.infra.adapters.out.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {
    // Dominio -> JPA (Para Guardar)
    public UserJpaEntity toJpaEntity(User user) {
        if (user == null) return null;

        UserJpaEntity entity = new UserJpaEntity();

        if (user.getId() != null) {
            entity.setId(user.getId());
        }

        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRole(user.getRole());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());

        // 1. Mapeo Seguro de PersonalInfo (Puede ser nulo en usuarios nuevos)
        if (user.getPersonalInfo() != null) {
            PersonalInfoEmbeddable infoJpa = new PersonalInfoEmbeddable();
            infoJpa.setFirstName(user.getPersonalInfo().firstName());
            infoJpa.setLastName(user.getPersonalInfo().lastName());
            infoJpa.setDocumentNumber(user.getPersonalInfo().documentNumber());
            infoJpa.setPhoneNumber(user.getPersonalInfo().phoneNumber());

            entity.setPersonalInfo(infoJpa);
        }

        // 2. Mapeo Seguro del Estado Fiscal
        entity.setTaxStatus(user.getTaxStatus());

        // 3. Mapeo de Direcciones
        if (user.getAddresses() != null) {
            for (Address domainAddress : user.getAddresses()) {
                AddressJpaEntity addressJpa = new AddressJpaEntity();
                addressJpa.setId(domainAddress.getId());
                addressJpa.setStreet(domainAddress.getStreet());
                addressJpa.setNumber(domainAddress.getNumber());
                addressJpa.setZipCode(domainAddress.getZipCode());
                addressJpa.setCity(domainAddress.getCity());
                addressJpa.setState(domainAddress.getState());
                addressJpa.setDefault(domainAddress.isDefault());

                // añade la dirección a la lista Y le setea el usuario padre
                entity.addAddress(addressJpa);
            }
        }

        // 4. Mapeo de Métodos de Pago (Tarjetas)
        if (user.getPaymentMethods() != null) {
            for (SavedPaymentMethod domainPayment : user.getPaymentMethods()) {
                SavedPaymentMethodJpaEntity paymentJpa = new SavedPaymentMethodJpaEntity();
                paymentJpa.setId(domainPayment.getId());
                paymentJpa.setToken(domainPayment.getToken());
                paymentJpa.setCardBrand(domainPayment.getCardBrand());
                paymentJpa.setLastFourDigits(domainPayment.getLastFourDigits());
                paymentJpa.setDefault(domainPayment.isDefault());

                // Mismo mapeo bidireccional
                entity.addPaymentMethod(paymentJpa);
            }
        }

        return entity;
    }

    // JPA -> Dominio (Para Leer)
    public User toDomainModel(UserJpaEntity entity) {
        if (entity == null) return null;

        // Si el perfil está incompleto, debe ser null.
        PersonalInfo personalInfo = null;
        if (entity.getPersonalInfo() != null && entity.getPersonalInfo().getFirstName() != null) {
            personalInfo = new PersonalInfo(
                    entity.getPersonalInfo().getFirstName(),
                    entity.getPersonalInfo().getLastName(),
                    entity.getPersonalInfo().getDocumentNumber(),
                    entity.getPersonalInfo().getPhoneNumber()
            );
        }

        // Mapeamos las direcciones
        List<Address> addresses = new ArrayList<>();
        if (entity.getAddresses() != null) {
            addresses = entity.getAddresses().stream()
                    .map(a -> new Address(a.getId(), a.getStreet(), a.getNumber(), a.getZipCode(), a.getCity(), a.getState(), a.isDefault()))
                    .collect(Collectors.toList()); // Usamos toList() normal
        }

        // Mapeamos los métodos de pago
        List<SavedPaymentMethod> paymentMethods = new ArrayList<>();
        if (entity.getPaymentMethods() != null) {
            paymentMethods = entity.getPaymentMethods().stream()
                    .map(p -> new SavedPaymentMethod(p.getId(), p.getToken(), p.getCardBrand(), p.getLastFourDigits(), p.isDefault()))
                    .collect(Collectors.toList()); // Usamos toList() normal
        }

        // Reconstrucción del Agregado puro
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRole(),
                personalInfo,
                entity.getTaxStatus(),
                addresses,
                paymentMethods,
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}
