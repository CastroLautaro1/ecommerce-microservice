package com.ecommerce.user_service.application.commands;

import com.ecommerce.user_service.domain.models.PersonalInfo;
import com.ecommerce.user_service.domain.models.TaxStatus;

import java.util.UUID;

public record UpdateProfileCommand(
        UUID userId,
        PersonalInfo personalInfo,
        TaxStatus taxStatus
) {
}
