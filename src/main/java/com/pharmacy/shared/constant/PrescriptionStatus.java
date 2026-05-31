package com.pharmacy.shared.constant;

public final class PrescriptionStatus {

    private PrescriptionStatus() {
        // Utility class — do not instantiate
    }

    public static final String RECEIVED = "RECEIVED";
    public static final String IN_PREPARATION = "IN_PREPARATION";
    public static final String READY = "READY";
    public static final String REJECTED = "REJECTED";
}
