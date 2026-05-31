package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.observer.PrescriptionStatusChangedEvent;
import com.pharmacy.pharmacist.observer.PatientNotificationListener;
import org.junit.jupiter.api.Test;

class PatientNotificationListenerTest {

    @Test
    void handle_shouldCompleteWithoutException() {
        PatientNotificationListener listener = new PatientNotificationListener();
        PrescriptionStatusChangedEvent event = new PrescriptionStatusChangedEvent(
                this,
                1L,
                "patient@example.com",
                "READY",
                "Test Pharmacy"
        );

        listener.handle(event);
    }
}
