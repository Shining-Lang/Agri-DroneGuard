package com.agriuav.backend.api.dto;

import com.agriuav.backend.domain.SprayApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SprayApplicationResponse(
        UUID id,
        String applicationNo,
        String applicantName,
        String villageCode,
        String parcelCode,
        String cropType,
        BigDecimal areaMu,
        String targetPest,
        LocalDate plannedDate,
        String status,
        OffsetDateTime createdAt
) {
    public static SprayApplicationResponse from(SprayApplication application) {
        return new SprayApplicationResponse(
                application.id(),
                application.applicationNo(),
                application.applicantName(),
                application.villageCode(),
                application.parcelCode(),
                application.cropType(),
                application.areaMu(),
                application.targetPest(),
                application.plannedDate(),
                application.status().name(),
                application.createdAt());
    }
}
