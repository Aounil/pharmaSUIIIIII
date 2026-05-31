package com.pharmacy.pharmacist.mapper;

import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.dto.PrescriptionHistoryItemDTO;
import com.pharmacy.pharmacist.dto.PrescriptionQueueItemDTO;
import com.pharmacy.shared.entity.Prescription;

public final class PrescriptionMapper {

    private PrescriptionMapper() {
        // Utility class — no instantiation
    }

    public static PrescriptionQueueItemDTO toQueueItem(Prescription p) {
        return new PrescriptionQueueItemDTO(
                p.getId(),
                getPatientEmail(p),
                p.getFilePath(),
                p.getStatus(),
                p.getCreatedAt()
        );
    }

    public static PrescriptionDetailDTO toDetail(Prescription p) {
        return new PrescriptionDetailDTO(
                p.getId(),
                getPatientEmail(p),
                p.getFilePath(),
                p.getStatus(),
                p.getTotalAmount(),
                p.getRejectionComment(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    public static PrescriptionHistoryItemDTO toHistoryItem(Prescription p) {
        return new PrescriptionHistoryItemDTO(
                p.getId(),
                getPatientEmail(p),
                p.getStatus(),
                p.getTotalAmount(),
                p.getUpdatedAt()
        );
    }

    private static String getPatientEmail(Prescription p) {
        if (p == null || p.getPatient() == null || p.getPatient().getEmail() == null) {
            return "unknown";
        }
        return p.getPatient().getEmail();
    }
}
