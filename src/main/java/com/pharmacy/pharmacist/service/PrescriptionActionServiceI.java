package com.pharmacy.pharmacist.service;

import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;

import java.math.BigDecimal;

public interface PrescriptionActionServiceI {

    PrescriptionDetailDTO accept(Long prescriptionId, Long pharmacyId, BigDecimal baseAmount, String pricingType);

    PrescriptionDetailDTO reject(Long prescriptionId, Long pharmacyId, String comment);

    PrescriptionDetailDTO markReady(Long prescriptionId, Long pharmacyId);
}
