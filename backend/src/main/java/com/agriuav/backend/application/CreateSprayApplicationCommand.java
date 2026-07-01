package com.agriuav.backend.application;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSprayApplicationCommand(
        String applicantName,
        String villageCode,
        String parcelCode,
        String cropType,
        BigDecimal areaMu,
        String targetPest,
        LocalDate plannedDate
) {
}
