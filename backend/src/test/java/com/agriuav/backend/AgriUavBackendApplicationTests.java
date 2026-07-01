package com.agriuav.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class AgriUavBackendApplicationTests {

    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:16-3.4")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("agri_uav_test")
            .withUsername("agri_uav")
            .withPassword("agri_uav_test");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void flywayCreatesSprayApplicationsTable() {
        Integer tableCount = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                  AND table_name = 'spray_applications'
                """, Integer.class);

        assertThat(tableCount).isEqualTo(1);
    }

    @Test
    void createsSprayApplication() throws Exception {
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "applicantName", "张三",
                "villageCode", "VILLAGE-001",
                "parcelCode", "PARCEL-001",
                "cropType", "小麦",
                "areaMu", new BigDecimal("500.00"),
                "targetPest", "蚜虫",
                "plannedDate", LocalDate.now().plusDays(1)));

        String responseBody = mockMvc.perform(post("/api/v1/spray-applications")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith(
                        "/api/v1/spray-applications/")))
                .andExpect(jsonPath("$.applicationNo", org.hamcrest.Matchers.startsWith("SA-")))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.areaMu").value(500.00))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String applicationNo = objectMapper.readTree(responseBody)
                .get("applicationNo")
                .asText();
        Map<String, Object> storedApplication = jdbcTemplate.queryForMap("""
                SELECT applicant_name, parcel_code, status
                FROM spray_applications
                WHERE application_no = ?
                """, applicationNo);

        assertThat(storedApplication)
                .containsEntry("applicant_name", "张三")
                .containsEntry("parcel_code", "PARCEL-001")
                .containsEntry("status", "SUBMITTED");
    }

    @Test
    void rejectsInvalidSprayApplication() throws Exception {
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "applicantName", " ",
                "villageCode", "VILLAGE-001",
                "parcelCode", "PARCEL-001",
                "cropType", "小麦",
                "areaMu", BigDecimal.ZERO,
                "targetPest", "蚜虫",
                "plannedDate", LocalDate.now().minusDays(1)));

        mockMvc.perform(post("/api/v1/spray-applications")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.applicantName").exists())
                .andExpect(jsonPath("$.fieldErrors.areaMu").exists())
                .andExpect(jsonPath("$.fieldErrors.plannedDate").exists());
    }
}
