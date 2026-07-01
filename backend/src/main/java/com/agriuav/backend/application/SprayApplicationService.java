package com.agriuav.backend.application;

import com.agriuav.backend.domain.SprayApplication;
import com.agriuav.backend.domain.SprayApplicationStatus;
import com.agriuav.backend.persistence.SprayApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Service
public class SprayApplicationService {

    private final SprayApplicationRepository repository;

    public SprayApplicationService(SprayApplicationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SprayApplication create(CreateSprayApplicationCommand command) {
        UUID id = UUID.randomUUID();
        SprayApplication application = new SprayApplication(
                id,
                generateApplicationNo(id),
                command.applicantName(),
                command.villageCode(),
                command.parcelCode(),
                command.cropType(),
                command.areaMu(),
                command.targetPest(),
                command.plannedDate(),
                SprayApplicationStatus.SUBMITTED,
                null,
                null);

        return repository.save(application);
    }

    private String generateApplicationNo(UUID id) {
        byte[] bytes = ByteBuffer.allocate(16)
                .putLong(id.getMostSignificantBits())
                .putLong(id.getLeastSignificantBits())
                .array();
        return "SA-" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
