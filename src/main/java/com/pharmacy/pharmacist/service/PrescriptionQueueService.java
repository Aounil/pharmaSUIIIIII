package com.pharmacy.pharmacist.service;

import com.pharmacy.pharmacist.dto.PrescriptionQueueItemDTO;
import com.pharmacy.pharmacist.mapper.PrescriptionMapper;
import com.pharmacy.pharmacist.repository.PharmacistPrescriptionRepository;
import com.pharmacy.shared.constant.PrescriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PrescriptionQueueService {

    private final PharmacistPrescriptionRepository repository;

    public PrescriptionQueueService(PharmacistPrescriptionRepository repository) {
        this.repository = repository;
    }

    public Page<PrescriptionQueueItemDTO> getQueue(Long pharmacyId, Pageable pageable) {
        return repository.findByPharmacyIdAndStatusOrderByCreatedAtAsc(pharmacyId,
                        PrescriptionStatus.RECEIVED,
                        pageable)
                .map(PrescriptionMapper::toQueueItem);
    }
}
