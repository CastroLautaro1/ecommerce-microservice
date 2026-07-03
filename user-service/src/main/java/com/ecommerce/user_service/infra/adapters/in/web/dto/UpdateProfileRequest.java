package com.ecommerce.user_service.infra.adapters.in.web.dto;

import com.ecommerce.user_service.domain.models.PersonalInfo;
import com.ecommerce.user_service.domain.models.TaxStatus;

public record UpdateProfileRequest(
        PersonalInfo personalInfo,
        TaxStatus taxStatus
) {
}
