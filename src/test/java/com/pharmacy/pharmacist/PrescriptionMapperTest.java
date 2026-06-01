package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.dto.PrescriptionDetailDTO;
import com.pharmacy.pharmacist.dto.PrescriptionHistoryItemDTO;
import com.pharmacy.pharmacist.dto.PrescriptionQueueItemDTO;
import com.pharmacy.pharmacist.mapper.PrescriptionMapper;
import com.pharmacy.shared.entity.Prescription;
import com.pharmacy.shared.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PrescriptionMapperTest {

    @Test
    void mapper_shouldUsePatientEmailWhenPresent() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 1, 2, 10, 0);
        Prescription prescription = prescriptionWithPatientEmail("patient@example.com");
        prescription.setId(1L);
        prescription.setFilePath("file.pdf");
        prescription.setStatus("READY");
        prescription.setTotalAmount(new BigDecimal("90.00"));
        prescription.setRejectionComment("Missing data");
        prescription.setCreatedAt(createdAt);
        prescription.setUpdatedAt(updatedAt);

        PrescriptionQueueItemDTO queueItem = PrescriptionMapper.toQueueItem(prescription);
        PrescriptionDetailDTO detail = PrescriptionMapper.toDetail(prescription);
        PrescriptionHistoryItemDTO historyItem = PrescriptionMapper.toHistoryItem(prescription);

        assertThat(queueItem.patientEmail()).isEqualTo("patient@example.com");
        assertThat(queueItem.createdAt()).isEqualTo(createdAt);
        assertThat(detail.patientEmail()).isEqualTo("patient@example.com");
        assertThat(detail.filePath()).isEqualTo("file.pdf");
        assertThat(detail.totalAmount()).isEqualByComparingTo("90.00");
        assertThat(detail.rejectionComment()).isEqualTo("Missing data");
        assertThat(detail.updatedAt()).isEqualTo(updatedAt);
        assertThat(historyItem.patientEmail()).isEqualTo("patient@example.com");
        assertThat(historyItem.status()).isEqualTo("READY");
    }

    @Test
    void mapper_shouldUseUnknownWhenPatientIsMissingOrEmailIsMissing() {
        Prescription noPatient = new Prescription();
        noPatient.setId(1L);

        Prescription noEmail = prescriptionWithPatientEmail(null);
        noEmail.setId(2L);

        assertThat(PrescriptionMapper.toQueueItem(noPatient).patientEmail()).isEqualTo("unknown");
        assertThat(PrescriptionMapper.toDetail(noEmail).patientEmail()).isEqualTo("unknown");
    }

    private Prescription prescriptionWithPatientEmail(String email) {
        User patient = new User();
        patient.setEmail(email);

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        return prescription;
    }
}
