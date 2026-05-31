package com.pharmacy.pharmacist.strategy;

public final class PricingStrategyFactory {

    private PricingStrategyFactory() {
        // Utility class — no instantiation
    }

    public static PricingStrategy getStrategy(String type) {
        if (type == null) {
            return new StandardPricingStrategy();
        }

        return switch (type.toUpperCase()) {
            case "INSURANCE" -> new InsurancePricingStrategy();
            case "REDUCED" -> new ReducedPricingStrategy();
            default -> new StandardPricingStrategy();
        };
    }
}
