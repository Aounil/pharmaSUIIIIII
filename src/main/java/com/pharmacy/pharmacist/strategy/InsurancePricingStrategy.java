package com.pharmacy.pharmacist.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InsurancePricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculate(BigDecimal baseAmount) {
        if (baseAmount == null) {
            return null;
        }
        return baseAmount.multiply(BigDecimal.valueOf(0.80))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
