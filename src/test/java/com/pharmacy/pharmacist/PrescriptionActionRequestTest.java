package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.dto.PrescriptionActionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PrescriptionActionRequestTest {

    @Test
    void gettersAndSetters_shouldExposeValuesAndDefaultPricingType() {
        PrescriptionActionRequest request = new PrescriptionActionRequest();
        request.setAction("ACCEPT");
        request.setComment("Ready to process");
        request.setTotalAmount(new BigDecimal("120.00"));

        assertThat(request.getAction()).isEqualTo("ACCEPT");
        assertThat(request.getComment()).isEqualTo("Ready to process");
        assertThat(request.getTotalAmount()).isEqualByComparingTo("120.00");
        assertThat(request.getPricingType()).isEqualTo("STANDARD");

        request.setPricingType("  ");
        assertThat(request.getPricingType()).isEqualTo("STANDARD");

        request.setPricingType("INSURANCE");
        assertThat(request.getPricingType()).isEqualTo("INSURANCE");
    }
}
