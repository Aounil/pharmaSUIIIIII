package com.pharmacy.pharmacist.observer;

import com.pharmacy.admin.factory.NotificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PatientNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(PatientNotificationListener.class);

    @Async
    @EventListener(PrescriptionStatusChangedEvent.class)
    public void handle(PrescriptionStatusChangedEvent event) {
        try {
            String message = buildMessage(event);
            NotificationFactory.createNotification("EMAIL", event.getPatientEmail(), message).send();
        } catch (Exception ex) {
            log.error("Failed to send patient notification for prescription {}: {}",
                    event.getPrescriptionId(), ex.getMessage(), ex);
        }
    }

    private String buildMessage(PrescriptionStatusChangedEvent event) {
        return switch (event.getNewStatus()) {
            case "IN_PREPARATION" -> String.format(
                    "Your prescription #%d is being prepared by %s.",
                    event.getPrescriptionId(), event.getPharmacyName());
            case "READY" -> String.format(
                    "Your prescription #%d is ready for pickup at %s.",
                    event.getPrescriptionId(), event.getPharmacyName());
            case "REJECTED" -> String.format(
                    "Your prescription #%d was rejected. Please contact %s for details.",
                    event.getPrescriptionId(), event.getPharmacyName());
            case "RECEIVED" -> String.format(
                    "Your prescription #%d has been received by %s.",
                    event.getPrescriptionId(), event.getPharmacyName());
            default -> String.format(
                    "Your prescription #%d status changed to %s at %s.",
                    event.getPrescriptionId(), event.getNewStatus(), event.getPharmacyName());
        };
    }
}
