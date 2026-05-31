package com.pharmacy.pharmacist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.pharmacist.controller.PrescriptionActionController;
import com.pharmacy.pharmacist.dto.PrescriptionActionRequest;
import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.security.PharmacistPrincipal;
import com.pharmacy.pharmacist.service.PrescriptionActionServiceI;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.shared.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PrescriptionActionController.class)
class PrescriptionActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrescriptionActionServiceI prescriptionActionService;

    @MockBean
    private com.pharmacy.shared.security.JwtUtil jwtUtil;

    @MockBean
    private com.pharmacy.shared.repository.UserRepository userRepository;

    @MockBean
    private PharmacistPrescriptionRepository prescriptionRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    void acceptPrescription_shouldReturnOkForValidRequest() throws Exception {
        PrescriptionActionRequest request = new PrescriptionActionRequest();
        request.setAction("ACCEPT");
        request.setTotalAmount(new BigDecimal("120.00"));
        request.setPricingType("INSURANCE");

        when(prescriptionActionService.accept(eq(1L), eq(20L), eq(new BigDecimal("120.00")), eq("INSURANCE")))
                .thenReturn(new PrescriptionDetailDTO(1L, "patient@example.com", "file.pdf", "IN_PREPARATION", new BigDecimal("96.00"), null, null, null));

        PharmacistPrincipal principal = new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_PHARMACIST", 20L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        mockMvc.perform(patch("/api/v1/pharmacist/prescriptions/1/accept")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PREPARATION"));
    }
}
