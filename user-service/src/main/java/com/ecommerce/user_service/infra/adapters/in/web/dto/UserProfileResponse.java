package com.ecommerce.user_service.infra.adapters.in.web.dto;

import com.ecommerce.user_service.domain.models.PersonalInfo;
import com.ecommerce.user_service.domain.models.TaxStatus;
import com.ecommerce.user_service.domain.models.User;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        boolean active,
        PersonalInfoResponse personalInfo,
        TaxStatus taxStatus
) {
    // Sub-record para mapear los datos personales de forma limpia
    public record PersonalInfoResponse(
            String firstName,
            String lastName,
            String documentNumber,
            String phoneNumber
    ) {}

    // Metodo para mappear desde el Usuario del dominio hacia el DTO
    public static UserProfileResponse fromDomain(User user) {
        PersonalInfo info = user.getPersonalInfo();
        PersonalInfoResponse infoResponse = null;

        if (info != null) {
            infoResponse = new PersonalInfoResponse(
                    info.firstName(),
                    info.lastName(),
                    info.documentNumber(),
                    info.phoneNumber()
            );
        }

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isActive(),
                infoResponse,
                user.getTaxStatus()
        );
    }
}
