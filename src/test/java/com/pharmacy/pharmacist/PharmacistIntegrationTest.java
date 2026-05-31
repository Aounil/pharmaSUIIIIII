package com.pharmacy.pharmacist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.shared.entity.Pharmacy;
import com.pharmacy.shared.entity.Prescription;
import com.pharmacy.shared.entity.User;
import com.pharmacy.admin.repository.AdminUserRepository;
import com.pharmacy.shared.repository.PharmacyRepository;
import com.pharmacy.shared.repository.PrescriptionRepository;
import com.pharmacy.shared.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PharmacistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User pharmacist;
    private Pharmacy pharmacy;
    private Prescription prescription;
    private String authHeader;

    @BeforeEach
    void setUp() throws Exception {
        prescriptionRepository.deleteAll();
        adminUserRepository.deleteAll();
        userRepository.deleteAll();
        pharmacyRepository.deleteAll();

        pharmacy = new Pharmacy();
        pharmacy.setName("Integration Pharmacy");
        pharmacy = pharmacyRepository.save(pharmacy);

        pharmacist = new User();
        pharmacist.setEmail("pharmacist@example.com");
        pharmacist.setPassword(passwordEncoder.encode("password"));
        pharmacist.setRole("ROLE_PHARMACIST");
        pharmacist = userRepository.save(pharmacist);

        User patient = new User();
        patient.setEmail("patient@example.com");
        patient.setPassword(passwordEncoder.encode("password"));
        patient.setRole("ROLE_PATIENT");
        patient = userRepository.save(patient);

        prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setPharmacy(pharmacy);
        prescription.setStatus("RECEIVED");
        prescription.setFilePath("prescription.pdf");
        prescription.setTotalAmount(new BigDecimal("140.00"));
        prescription = prescriptionRepository.save(prescription);

        String loginBody = objectMapper.writeValueAsString(new PharmacistLoginPayload(pharmacist.getEmail(), "password", pharmacy.getId()));
        String response = mockMvc.perform(post("/api/v1/pharmacist/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode responseNode = objectMapper.readTree(response);
        authHeader = "Bearer " + responseNode.get("token").asText();
    }

    @Test
    void queueEndpoint_shouldReturnReceivedPrescription() throws Exception {
        mockMvc.perform(get("/api/v1/pharmacist/prescriptions?page=0&size=10")
                        .header("Authorization", authHeader)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(prescription.getId()))
                .andExpect(jsonPath("$.content[0].status").value("RECEIVED"));
    }

    @Test
    void acceptEndpoint_shouldTransitionToInPreparation() throws Exception {
        String body = objectMapper.writeValueAsString(
                new TestActionPayload("ACCEPT", null, new BigDecimal("140.00"), "INSURANCE")
        );

        mockMvc.perform(patch("/api/v1/pharmacist/prescriptions/" + prescription.getId() + "/accept")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PREPARATION"));

        Prescription updated = prescriptionRepository.findById(prescription.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo("IN_PREPARATION");
    }

    @Test
    void readyEndpoint_shouldReturnReadyAfterPreparation() throws Exception {
        prescription.setStatus("IN_PREPARATION");
        prescriptionRepository.save(prescription);

        mockMvc.perform(patch("/api/v1/pharmacist/prescriptions/" + prescription.getId() + "/ready")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"));

        assertThat(prescriptionRepository.findById(prescription.getId()).orElseThrow().getStatus()).isEqualTo("READY");
    }

    @Test
    void shouldReturnForbiddenWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/pharmacist/prescriptions/queue?page=0&size=10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private static final class PharmacistLoginPayload {
        public String email;
        public String password;
        public Long pharmacyId;

        public PharmacistLoginPayload(String email, String password, Long pharmacyId) {
            this.email = email;
            this.password = password;
            this.pharmacyId = pharmacyId;
        }
    }

    private static final class TestActionPayload {
        public String action;
        public String comment;
        public BigDecimal totalAmount;
        public String pricingType;

        public TestActionPayload(String action, String comment, BigDecimal totalAmount, String pricingType) {
            this.action = action;
            this.comment = comment;
            this.totalAmount = totalAmount;
            this.pricingType = pricingType;
        }
    }
}
