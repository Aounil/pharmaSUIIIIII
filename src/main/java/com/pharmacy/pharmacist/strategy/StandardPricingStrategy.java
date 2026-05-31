package com.pharmacy.pharmacist.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculate(BigDecimal baseAmount) {
        return baseAmount == null ? null : baseAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
