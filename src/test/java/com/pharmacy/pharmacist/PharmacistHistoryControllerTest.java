package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.controller.PharmacistHistoryController;
import com.pharmacy.pharmacist.dto.PrescriptionHistoryItemDTO;
import com.pharmacy.pharmacist.security.PharmacistPrincipal;
import com.pharmacy.pharmacist.service.PharmacistHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PharmacistHistoryController.class)
class PharmacistHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.pharmacy.shared.security.JwtUtil jwtUtil;

    @MockBean
    private com.pharmacy.shared.repository.UserRepository userRepository;

    @MockBean
    private PharmacistHistoryService historyService;

    @Test
    void getHistory_shouldReturnHistoryPage() throws Exception {
        PharmacistPrincipal principal = new PharmacistPrincipal(1L, "pharmacist@example.com", "ROLE_PHARMACIST", 10L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        var dto = new PrescriptionHistoryItemDTO(1L, "patient@example.com", "READY", new BigDecimal("100.00"), LocalDateTime.now());
        Mockito.when(historyService.getHistory(10L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/pharmacist/prescriptions/history?page=0&size=10")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("READY"));
    }
}
