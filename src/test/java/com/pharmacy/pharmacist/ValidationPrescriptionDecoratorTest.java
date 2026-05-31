package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.service.PrescriptionActionServiceI;
import com.pharmacy.pharmacist.service.ValidationPrescriptionDecorator;
import com.pharmacy.shared.entity.Prescription;
import com.pharmacy.shared.constant.PrescriptionStatus;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationPrescriptionDecoratorTest {

    @Mock
    private PrescriptionActionServiceI delegate;

    @Mock
    private PharmacistPrescriptionRepository repository;

    @InjectMocks
    private ValidationPrescriptionDecorator decorator;

    private Prescription prescription;

    @BeforeEach
    void setUp() {
        prescription = new Prescription();
        prescription.setId(1L);
        prescription.setStatus(PrescriptionStatus.RECEIVED);
    }

    @Test
    void accept_shouldDelegateWhenPrescriptionIsReceived() {
        when(repository.findByIdAndPharmacyId(1L, 2L)).thenReturn(Optional.of(prescription));
        PrescriptionDetailDTO expected = new PrescriptionDetailDTO(1L, "patient@example.com", null, "RECEIVED", null, null, null, null);
        when(delegate.accept(1L, 2L, new BigDecimal("50.00"), "INSURANCE")).thenReturn(expected);

        PrescriptionDetailDTO result = decorator.accept(1L, 2L, new BigDecimal("50.00"), "INSURANCE");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void accept_shouldThrowWhenInPreparation() {
        prescription.setStatus(PrescriptionStatus.IN_PREPARATION);
        when(repository.findByIdAndPharmacyId(1L, 2L)).thenReturn(Optional.of(prescription));

        assertThatThrownBy(() -> decorator.accept(1L, 2L, new BigDecimal("50.00"), "INSURANCE"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Prescription is not in RECEIVED status");
    }

    @Test
    void reject_shouldThrowWhenReady() {
        prescription.setStatus(PrescriptionStatus.READY);
        when(repository.findByIdAndPharmacyId(1L, 2L)).thenReturn(Optional.of(prescription));

        assertThatThrownBy(() -> decorator.reject(1L, 2L, "Bad info"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Prescription is not in RECEIVED status");
    }

    @Test
    void reject_shouldThrowWhenCommentMissing() {
        when(repository.findByIdAndPharmacyId(1L, 2L)).thenReturn(Optional.of(prescription));

        assertThatThrownBy(() -> decorator.reject(1L, 2L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rejection comment must not be blank");
    }

    @Test
    void markReady_shouldThrowWhenNotInPreparation() {
        prescription.setStatus(PrescriptionStatus.RECEIVED);
        when(repository.findByIdAndPharmacyId(1L, 2L)).thenReturn(Optional.of(prescription));

        assertThatThrownBy(() -> decorator.markReady(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must be IN_PREPARATION");
    }
}
