package com.pharmacy.pharmacist.observer;

import org.springframework.context.ApplicationEvent;

public class PrescriptionStatusChangedEvent extends ApplicationEvent {

    private final Long prescriptionId;
    private final String patientEmail;
    private final String newStatus;
    private final String pharmacyName;

    public PrescriptionStatusChangedEvent(Object source,
                                          Long prescriptionId,
                                          String patientEmail,
                                          String newStatus,
                                          String pharmacyName) {
        super(source);
        this.prescriptionId = prescriptionId;
        this.patientEmail = patientEmail;
        this.newStatus = newStatus;
        this.pharmacyName = pharmacyName;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public String getPharmacyName() {
        return pharmacyName;
    }
}
