package com.agriuav.backend.application;

import com.agriuav.backend.domain.SprayApplication;
import com.agriuav.backend.domain.SprayApplicationStatus;
import com.agriuav.backend.persistence.SprayApplicationRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SprayApplicationServiceTests {

    @Test
    void createsApplicationWithSubmittedStatusAndUniqueNumber() {
        CapturingSprayApplicationRepository repository =
                new CapturingSprayApplicationRepository();
        SprayApplicationService service = new SprayApplicationService(repository);
        CreateSprayApplicationCommand command = new CreateSprayApplicationCommand(
                "Applicant",
                "VILLAGE-001",
                "PARCEL-001",
                "WHEAT",
                new BigDecimal("500.00"),
                "APHID",
                LocalDate.now().plusDays(1));

        SprayApplication result = service.create(command);

        assertThat(result).isSameAs(repository.savedApplication);
        assertThat(result.id()).isNotNull();
        assertThat(result.applicationNo())
                .startsWith("SA-")
                .hasSize(25);
        assertThat(result.status()).isEqualTo(SprayApplicationStatus.SUBMITTED);
        assertThat(result.applicantName()).isEqualTo("Applicant");
    }

    private static final class CapturingSprayApplicationRepository
            extends SprayApplicationRepository {

        private SprayApplication savedApplication;

        private CapturingSprayApplicationRepository() {
            super(null);
        }

        @Override
        public SprayApplication save(SprayApplication application) {
            this.savedApplication = application;
            return application;
        }
    }
}
