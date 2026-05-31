package com.pharmacy.pharmacist.strategy;

import java.math.BigDecimal;

/**
 * Strategy pattern contract for pharmacist pricing calculations.
 */
public interface PricingStrategy {

    /**
     * Calculate the final amount based on a base amount.
     *
     * @param baseAmount the original amount for the prescription
     * @return final calculated amount with scale 2 precision
     */
    BigDecimal calculate(BigDecimal baseAmount);
}
