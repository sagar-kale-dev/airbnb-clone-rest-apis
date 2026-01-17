package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.entity.Hotel;
import co.in.sagarkale.airBnbClone.entity.Inventory;

import java.util.List;

public interface PricingUpdateService {
    void updatePrices();
    void updateHotelPrices(Hotel hotel);
    void updateHotelMinPrices(Hotel hotel, List<Inventory> inventoryList);
    void updateInventoryPrices(List<Inventory> inventoryList);
}
