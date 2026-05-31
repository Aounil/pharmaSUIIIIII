package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.dto.PrescriptionQueueItemDTO;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.pharmacist.service.PrescriptionQueueService;
import com.pharmacy.shared.entity.Pharmacy;
import com.pharmacy.shared.entity.Prescription;
import com.pharmacy.shared.entity.User;
import com.pharmacy.shared.constant.PrescriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrescriptionQueueServiceTest {

    @Mock
    private PharmacistPrescriptionRepository repository;

    @InjectMocks
    private PrescriptionQueueService service;

    private Prescription receivedPrescription;
    private Prescription secondPrescription;

    @BeforeEach
    void setUp() {
        User patient = new User();
        patient.setId(10L);
        patient.setEmail("patient@example.com");

        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setId(20L);
        pharmacy.setName("Test Pharmacy");

        receivedPrescription = new Prescription();
        receivedPrescription.setId(1L);
        receivedPrescription.setPatient(patient);
        receivedPrescription.setPharmacy(pharmacy);
        receivedPrescription.setStatus(PrescriptionStatus.RECEIVED);
        receivedPrescription.setCreatedAt(LocalDateTime.of(2026, 1, 1, 10, 0));

        secondPrescription = new Prescription();
        secondPrescription.setId(2L);
        secondPrescription.setPatient(patient);
        secondPrescription.setPharmacy(pharmacy);
        secondPrescription.setStatus(PrescriptionStatus.RECEIVED);
        secondPrescription.setCreatedAt(LocalDateTime.of(2026, 1, 1, 11, 0));
    }

    @Test
    void getQueue_shouldReturnMappedDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Prescription> page = new PageImpl<>(List.of(receivedPrescription, secondPrescription), pageable, 2);
        when(repository.findByPharmacyIdAndStatusOrderByCreatedAtAsc(20L, PrescriptionStatus.RECEIVED, pageable))
                .thenReturn(page);

        Page<PrescriptionQueueItemDTO> result = service.getQueue(20L, pageable);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).id()).isEqualTo(1L);
        assertThat(result.getContent().get(0).patientEmail()).isEqualTo("patient@example.com");
    }

    @Test
    void getQueue_shouldReturnEmptyPageWhenNoPrescriptions() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByPharmacyIdAndStatusOrderByCreatedAtAsc(20L, PrescriptionStatus.RECEIVED, pageable))
                .thenReturn(Page.empty(pageable));

        Page<PrescriptionQueueItemDTO> result = service.getQueue(20L, pageable);

        assertThat(result).isEmpty();
    }
}
