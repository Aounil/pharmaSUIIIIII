package com.pharmacy.pharmacist;

import com.pharmacy.pharmacist.strategy.InsurancePricingStrategy;
import com.pharmacy.pharmacist.strategy.PricingStrategyFactory;
import com.pharmacy.pharmacist.strategy.ReducedPricingStrategy;
import com.pharmacy.pharmacist.strategy.StandardPricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PricingStrategyTest {

    @Test
    void standardStrategy_shouldReturnBaseAmount() {
        BigDecimal result = new StandardPricingStrategy().calculate(new BigDecimal("100.00"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void insuranceStrategy_shouldApplyTwentyPercentDiscount() {
        BigDecimal result = new InsurancePricingStrategy().calculate(new BigDecimal("100.00"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("80.00"));
    }

    @Test
    void reducedStrategy_shouldApplyFiftyPercentDiscount() {
        BigDecimal result = new ReducedPricingStrategy().calculate(new BigDecimal("100.00"));
        assertThat(result).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void pricingStrategyFactory_shouldReturnCorrectStrategyForInsurance() {
        assertThat(PricingStrategyFactory.getStrategy("INSURANCE")).isInstanceOf(InsurancePricingStrategy.class);
    }

    @Test
    void pricingStrategyFactory_shouldDefaultToStandardStrategyForUnknownType() {
        assertThat(PricingStrategyFactory.getStrategy("UNKNOWN")).isInstanceOf(StandardPricingStrategy.class);
    }
}
