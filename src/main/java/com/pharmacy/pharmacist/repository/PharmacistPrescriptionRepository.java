package com.pharmacy.pharmacist.repository;

import com.pharmacy.shared.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PharmacistPrescriptionRepository extends JpaRepository<Prescription, Long> {

    Page<Prescription> findByPharmacyIdAndStatusOrderByCreatedAtAsc(Long pharmacyId, String status, Pageable pageable);

    Page<Prescription> findByPharmacyIdOrderByUpdatedAtDesc(Long pharmacyId, Pageable pageable);

    Optional<Prescription> findByIdAndPharmacyId(Long id, Long pharmacyId);
}
