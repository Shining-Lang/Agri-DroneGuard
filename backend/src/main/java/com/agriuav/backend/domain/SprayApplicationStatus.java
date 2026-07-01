package com.agriuav.backend.domain;

public enum SprayApplicationStatus {
    SUBMITTED,
    VALIDATING,
    VALIDATED,
    PLANNING,
    AWAITING_APPROVAL,
    APPROVED,
    REJECTED,
    EXECUTING,
    COMPLETED,
    FAILED,
    CANCELLED
}
