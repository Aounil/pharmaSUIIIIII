package com.pharmacy.pharmacist.service;

import com.pharmacy.shared.constant.PrescriptionStatus;
import com.pharmacy.shared.entity.Prescription;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.pharmacist.service.PrescriptionActionServiceI;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Decorator pattern — wraps PrescriptionActionService with pre-condition validation
 * without modifying the core business logic.
 */
@Primary
@Service
@Transactional
public class ValidationPrescriptionDecorator implements PrescriptionActionServiceI {

    private final PrescriptionActionServiceI delegate;
    private final PharmacistPrescriptionRepository repository;

    public ValidationPrescriptionDecorator(PrescriptionActionServiceI delegate,
                                           PharmacistPrescriptionRepository repository) {
        this.delegate = delegate;
        this.repository = repository;
    }

    @Override
    public PrescriptionDetailDTO accept(Long prescriptionId, Long pharmacyId, BigDecimal baseAmount, String pricingType) {
        Prescription prescription = fetchPrescription(prescriptionId, pharmacyId);
        if (!PrescriptionStatus.RECEIVED.equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Prescription is not in RECEIVED status — current: " + prescription.getStatus());
        }
        return delegate.accept(prescriptionId, pharmacyId, baseAmount, pricingType);
    }

    @Override
    public PrescriptionDetailDTO reject(Long prescriptionId, Long pharmacyId, String comment) {
        Prescription prescription = fetchPrescription(prescriptionId, pharmacyId);
        if (!PrescriptionStatus.RECEIVED.equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Prescription is not in RECEIVED status — current: " + prescription.getStatus());
        }
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Rejection comment must not be blank");
        }
        return delegate.reject(prescriptionId, pharmacyId, comment);
    }

    @Override
    public PrescriptionDetailDTO markReady(Long prescriptionId, Long pharmacyId) {
        Prescription prescription = fetchPrescription(prescriptionId, pharmacyId);
        if (!PrescriptionStatus.IN_PREPARATION.equalsIgnoreCase(prescription.getStatus())) {
            throw new IllegalStateException("Prescription must be IN_PREPARATION to mark ready — current: " + prescription.getStatus());
        }
        return delegate.markReady(prescriptionId, pharmacyId);
    }

    private Prescription fetchPrescription(Long prescriptionId, Long pharmacyId) {
        return repository.findByIdAndPharmacyId(prescriptionId, pharmacyId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));
    }
}
