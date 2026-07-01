package com.agriuav.backend.api;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        Map<String, String> fieldErrors
) {
}
