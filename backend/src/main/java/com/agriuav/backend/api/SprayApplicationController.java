package com.agriuav.backend.api;

import com.agriuav.backend.api.dto.CreateSprayApplicationRequest;
import com.agriuav.backend.api.dto.SprayApplicationResponse;
import com.agriuav.backend.application.CreateSprayApplicationCommand;
import com.agriuav.backend.application.SprayApplicationService;
import com.agriuav.backend.domain.SprayApplication;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/spray-applications")
public class SprayApplicationController {

    private final SprayApplicationService service;

    public SprayApplicationController(SprayApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SprayApplicationResponse> create(
            @Valid @RequestBody CreateSprayApplicationRequest request) {
        SprayApplication application = service.create(new CreateSprayApplicationCommand(
                request.applicantName(),
                request.villageCode(),
                request.parcelCode(),
                request.cropType(),
                request.areaMu(),
                request.targetPest(),
                request.plannedDate()));

        return ResponseEntity
                .created(URI.create("/api/v1/spray-applications/" + application.id()))
                .body(SprayApplicationResponse.from(application));
    }
}
