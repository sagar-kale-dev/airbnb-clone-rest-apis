package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.entity.Hotel;
import co.in.sagarkale.airBnbClone.entity.HotelMinPrice;
import co.in.sagarkale.airBnbClone.entity.Inventory;
import co.in.sagarkale.airBnbClone.repository.HotelMinPriceRepository;
import co.in.sagarkale.airBnbClone.repository.HotelRepository;
import co.in.sagarkale.airBnbClone.repository.InventoryRepository;
import co.in.sagarkale.airBnbClone.strategy.pricing.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingUpdateServiceImpl implements PricingUpdateService{

    private final PricingService pricingService;
    private final InventoryRepository inventoryRepository;
    private final HotelRepository hotelRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void updatePrices() {
//        Hotels get are paginated so load one by one with page size 100
//        Then update individuals hotel price
//        Iterate Over pages until all hotels not fetched

        int page = 0;
        int pageSize = 100;

        while(true){
            Page<Hotel> hotelPage = hotelRepository.findAll(PageRequest.of(page, pageSize));
            if(hotelPage.isEmpty()){
                break;
            }
            hotelPage.forEach(this::updateHotelPrices);
            page++;
        }
    }

    @Override
    public void updateHotelPrices(Hotel hotel) {
//        Find all inventories of hotel for next 90 days and update their dynamic pricing and add entry in min price

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(90);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel, startDate, endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMinPrices(hotel, inventoryList);
    }

    @Override
    public void updateHotelMinPrices(Hotel hotel, List<Inventory> inventoryList) {
//        Loop inventories
//            - For each date, remember the cheapest price seen so far
//        Loop dates
//            - Create or update HotelMinPrice
//        Save all

        // 1. Find minimum price per date
        Map<LocalDate, BigDecimal> dailyMinPrices = new HashMap<>();
        for (Inventory inventory: inventoryList){
            LocalDate date = inventory.getDate();
            BigDecimal price = inventory.getPrice();

            if(!dailyMinPrices.containsKey(date) || (dailyMinPrices.get(date).compareTo(price) < 0)){
                  dailyMinPrices.put(date, price);
            }
        }

        // 2. Create / update HotelMinPrice entities
        List<HotelMinPrice> hotelMinPriceList = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> entry:dailyMinPrices.entrySet()){
            LocalDate date = entry.getKey();
            BigDecimal price = entry.getValue();

            HotelMinPrice hotelMinPrice = hotelMinPriceRepository
                    .findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setPrice(price);

            hotelMinPriceList.add(hotelMinPrice);
        }

        // 3. Save everything
        hotelMinPriceRepository.saveAll(hotelMinPriceList);
    }

    @Override
    public void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);
    }
}
