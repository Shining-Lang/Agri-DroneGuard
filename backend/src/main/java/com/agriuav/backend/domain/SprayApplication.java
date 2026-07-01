package com.agriuav.backend.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SprayApplication(
        UUID id,
        String applicationNo,
        String applicantName,
        String villageCode,
        String parcelCode,
        String cropType,
        BigDecimal areaMu,
        String targetPest,
        LocalDate plannedDate,
        SprayApplicationStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
