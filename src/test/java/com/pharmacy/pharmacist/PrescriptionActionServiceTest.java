package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.observer.PrescriptionStatusChangedEvent;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.pharmacist.service.PrescriptionActionService;
import com.pharmacy.shared.constant.PrescriptionStatus;
import com.pharmacy.shared.entity.Pharmacy;
import com.pharmacy.shared.entity.Prescription;
import com.pharmacy.shared.entity.User;
import com.pharmacy.shared.repository.PharmacyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionActionServiceTest {

    @Mock
    private PharmacistPrescriptionRepository repository;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private PharmacyRepository pharmacyRepository;

    @InjectMocks
    private PrescriptionActionService service;

    private Prescription currentPrescription;
    private Pharmacy pharmacy;

    @BeforeEach
    void setUp() {
        User patient = new User();
        patient.setId(10L);
        patient.setEmail("patient@example.com");

        pharmacy = new Pharmacy();
        pharmacy.setId(20L);
        pharmacy.setName("Test Pharmacy");

        currentPrescription = new Prescription();
        currentPrescription.setId(1L);
        currentPrescription.setPatient(patient);
        currentPrescription.setPharmacy(pharmacy);
        currentPrescription.setStatus(PrescriptionStatus.RECEIVED);
    }

    @Test
    void acceptPrescription_shouldUpdateStatusAndPublishEvent() {
        when(repository.findByIdAndPharmacyId(1L, 20L)).thenReturn(Optional.of(currentPrescription));
        when(pharmacyRepository.findById(20L)).thenReturn(Optional.of(pharmacy));
        when(repository.save(any(Prescription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrescriptionDetailDTO result = service.accept(1L, 20L, new BigDecimal("100.00"), "INSURANCE");

        assertThat(result.status()).isEqualTo(PrescriptionStatus.IN_PREPARATION);
        assertThat(result.totalAmount()).isEqualByComparingTo(new BigDecimal("80.00"));

        ArgumentCaptor<Prescription> prescriptionCaptor = ArgumentCaptor.forClass(Prescription.class);
        verify(repository).save(prescriptionCaptor.capture());
        assertThat(prescriptionCaptor.getValue().getStatus()).isEqualTo(PrescriptionStatus.IN_PREPARATION);

        verify(publisher, times(1)).publishEvent(any(PrescriptionStatusChangedEvent.class));
    }

    @Test
    void rejectPrescription_shouldSetRejectedStatusAndComment() {
        when(repository.findByIdAndPharmacyId(1L, 20L)).thenReturn(Optional.of(currentPrescription));
        when(repository.save(any(Prescription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrescriptionDetailDTO result = service.reject(1L, 20L, "Missing data");

        assertThat(result.status()).isEqualTo(PrescriptionStatus.REJECTED);
        assertThat(result.rejectionComment()).isEqualTo("Missing data");
    }

    @Test
    void markReady_shouldSetReadyStatus() {
        currentPrescription.setStatus(PrescriptionStatus.IN_PREPARATION);
        when(repository.findByIdAndPharmacyId(1L, 20L)).thenReturn(Optional.of(currentPrescription));
        when(repository.save(any(Prescription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PrescriptionDetailDTO result = service.markReady(1L, 20L);

        assertThat(result.status()).isEqualTo(PrescriptionStatus.READY);
        verify(repository).save(any(Prescription.class));
    }

    @Test
    void acceptPrescription_shouldThrowWhenNotFound() {
        when(repository.findByIdAndPharmacyId(1L, 20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.accept(1L, 20L, new BigDecimal("100.00"), "INSURANCE"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
