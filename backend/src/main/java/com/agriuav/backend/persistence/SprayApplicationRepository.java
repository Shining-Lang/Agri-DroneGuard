package com.agriuav.backend.persistence;

import com.agriuav.backend.domain.SprayApplication;
import com.agriuav.backend.domain.SprayApplicationStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public class SprayApplicationRepository {

    private final JdbcTemplate jdbcTemplate;

    public SprayApplicationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SprayApplication save(SprayApplication application) {
        return jdbcTemplate.queryForObject("""
                INSERT INTO spray_applications (
                    id,
                    application_no,
                    applicant_name,
                    village_code,
                    parcel_code,
                    crop_type,
                    area_mu,
                    target_pest,
                    planned_date,
                    status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING
                    id,
                    application_no,
                    applicant_name,
                    village_code,
                    parcel_code,
                    crop_type,
                    area_mu,
                    target_pest,
                    planned_date,
                    status,
                    created_at,
                    updated_at
                """, this::mapRow,
                application.id(),
                application.applicationNo(),
                application.applicantName(),
                application.villageCode(),
                application.parcelCode(),
                application.cropType(),
                application.areaMu(),
                application.targetPest(),
                application.plannedDate(),
                application.status().name());
    }

    private SprayApplication mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new SprayApplication(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("application_no"),
                resultSet.getString("applicant_name"),
                resultSet.getString("village_code"),
                resultSet.getString("parcel_code"),
                resultSet.getString("crop_type"),
                resultSet.getBigDecimal("area_mu"),
                resultSet.getString("target_pest"),
                resultSet.getObject("planned_date", LocalDate.class),
                SprayApplicationStatus.valueOf(resultSet.getString("status")),
                resultSet.getObject("created_at", OffsetDateTime.class),
                resultSet.getObject("updated_at", OffsetDateTime.class));
    }
}
