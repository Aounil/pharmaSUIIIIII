package com.pharmacy.pharmacist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PrescriptionHistoryItemDTO(
        Long id,
        String patientEmail,
        String status,
        BigDecimal totalAmount,
        LocalDateTime updatedAt
) {
}
