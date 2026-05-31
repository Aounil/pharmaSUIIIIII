package com.pharmacy.pharmacist.service;

import com.pharmacy.pharmacist.dto.PrescriptionHistoryItemDTO;
import com.pharmacy.pharmacist.mapper.PrescriptionMapper;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PharmacistHistoryService {

    private final PharmacistPrescriptionRepository repository;

    public PharmacistHistoryService(PharmacistPrescriptionRepository repository) {
        this.repository = repository;
    }

    public Page<PrescriptionHistoryItemDTO> getHistory(Long pharmacyId, Pageable pageable) {
        return repository.findByPharmacyIdOrderByUpdatedAtDesc(pharmacyId, pageable)
                .map(PrescriptionMapper::toHistoryItem);
    }
}
