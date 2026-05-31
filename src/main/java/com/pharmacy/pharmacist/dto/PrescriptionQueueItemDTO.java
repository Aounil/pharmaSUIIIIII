package com.pharmacy.pharmacist.dto;

import java.time.LocalDateTime;

public record PrescriptionQueueItemDTO(
        Long id,
        String patientEmail,
        String filePath,
        String status,
        LocalDateTime createdAt
) {
}
