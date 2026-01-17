package co.in.sagarkale.airBnbClone.strategy.pricing;

import co.in.sagarkale.airBnbClone.entity.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
