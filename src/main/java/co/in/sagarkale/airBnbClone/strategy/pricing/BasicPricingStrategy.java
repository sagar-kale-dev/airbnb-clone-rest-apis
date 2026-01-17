package co.in.sagarkale.airBnbClone.strategy.pricing;

import co.in.sagarkale.airBnbClone.entity.Inventory;

import java.math.BigDecimal;

public class BasicPricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
