CREATE TABLE spray_applications (
    id UUID PRIMARY KEY,
    application_no VARCHAR(32) NOT NULL UNIQUE,
    applicant_name VARCHAR(100) NOT NULL,
    village_code VARCHAR(64) NOT NULL,
    parcel_code VARCHAR(64) NOT NULL,
    crop_type VARCHAR(64) NOT NULL,
    area_mu NUMERIC(12, 2) NOT NULL,
    target_pest VARCHAR(100) NOT NULL,
    planned_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_spray_applications_area_positive CHECK (area_mu > 0),
    CONSTRAINT ck_spray_applications_status CHECK (
        status IN (
            'SUBMITTED',
            'VALIDATING',
            'VALIDATED',
            'PLANNING',
            'AWAITING_APPROVAL',
            'APPROVED',
            'REJECTED',
            'EXECUTING',
            'COMPLETED',
            'FAILED',
            'CANCELLED'
        )
    )
);

CREATE INDEX idx_spray_applications_status_created_at
    ON spray_applications (status, created_at DESC);

CREATE INDEX idx_spray_applications_parcel_code
    ON spray_applications (parcel_code);
