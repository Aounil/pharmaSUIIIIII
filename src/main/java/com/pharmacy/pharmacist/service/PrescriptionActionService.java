package com.pharmacy.pharmacist.service;

import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.mapper.PrescriptionMapper;
import com.pharmacy.pharmacist.observer.PrescriptionStatusChangedEvent;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.shared.constant.PrescriptionStatus;
import com.pharmacy.shared.entity.Pharmacy;
import com.pharmacy.shared.entity.Prescription;
import com.pharmacy.shared.repository.PharmacyRepository;
import com.pharmacy.pharmacist.strategy.PricingStrategyFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service("prescriptionActionService")
@Transactional
public class PrescriptionActionService implements PrescriptionActionServiceI {

    private final PharmacistPrescriptionRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final PharmacyRepository pharmacyRepository;

    public PrescriptionActionService(PharmacistPrescriptionRepository repository,
                                     ApplicationEventPublisher eventPublisher,
                                     PharmacyRepository pharmacyRepository) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.pharmacyRepository = pharmacyRepository;
    }

    @Override
    public PrescriptionDetailDTO accept(Long prescriptionId, Long pharmacyId, BigDecimal baseAmount, String pricingType) {
        Prescription prescription = repository.findByIdAndPharmacyId(prescriptionId, pharmacyId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));

        prescription.setStatus(PrescriptionStatus.IN_PREPARATION);
        if (baseAmount != null) {
            prescription.setTotalAmount(PricingStrategyFactory.getStrategy(pricingType)
                    .calculate(baseAmount));
        }

        Prescription saved = repository.save(prescription);
        String pharmacyName = fetchPharmacyName(pharmacyId);
        eventPublisher.publishEvent(new PrescriptionStatusChangedEvent(this,
                saved.getId(),
                saved.getPatient().getEmail(),
                PrescriptionStatus.IN_PREPARATION,
                pharmacyName));
        return PrescriptionMapper.toDetail(saved);
    }

    @Override
    public PrescriptionDetailDTO reject(Long prescriptionId, Long pharmacyId, String comment) {
        Prescription prescription = repository.findByIdAndPharmacyId(prescriptionId, pharmacyId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));

        prescription.setStatus(PrescriptionStatus.REJECTED);
        prescription.setRejectionComment(comment);

        Prescription saved = repository.save(prescription);
        String pharmacyName = fetchPharmacyName(pharmacyId);
        eventPublisher.publishEvent(new PrescriptionStatusChangedEvent(this,
                saved.getId(),
                saved.getPatient().getEmail(),
                PrescriptionStatus.REJECTED,
                pharmacyName));
        return PrescriptionMapper.toDetail(saved);
    }

    @Override
    public PrescriptionDetailDTO markReady(Long prescriptionId, Long pharmacyId) {
        Prescription prescription = repository.findByIdAndPharmacyId(prescriptionId, pharmacyId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription not found"));

        prescription.setStatus(PrescriptionStatus.READY);

        Prescription saved = repository.save(prescription);
        String pharmacyName = fetchPharmacyName(pharmacyId);
        eventPublisher.publishEvent(new PrescriptionStatusChangedEvent(this,
                saved.getId(),
                saved.getPatient().getEmail(),
                PrescriptionStatus.READY,
                pharmacyName));
        return PrescriptionMapper.toDetail(saved);
    }

    private String fetchPharmacyName(Long pharmacyId) {
        return pharmacyRepository.findById(pharmacyId)
                .map(Pharmacy::getName)
                .orElse("the pharmacy");
    }
}
