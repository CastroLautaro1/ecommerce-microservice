package com.ecommerce.user_service.domain.ports.in;

import com.ecommerce.user_service.domain.models.PersonalInfo;
import com.ecommerce.user_service.domain.models.TaxStatus;

public record UpdateProfileCommand(
        Long userId,
        PersonalInfo personalInfo,
        TaxStatus taxStatus
) {
}
