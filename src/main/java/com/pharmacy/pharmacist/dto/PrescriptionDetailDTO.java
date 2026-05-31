package com.pharmacy.pharmacist.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PrescriptionDetailDTO(
        Long id,
        String patientEmail,
        String filePath,
        String status,
        BigDecimal totalAmount,
        String rejectionComment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
