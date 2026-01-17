package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.*;
import co.in.sagarkale.airBnbClone.entity.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);
    Page<HotelPriceDto> searchHotels(HotelSearchReqDto hotelSearchReqDto);
    List<InventoryDto> getAllInventoryByRoomId(Long roomId);
    void updateInventory(Long roomId, UpdateInventoryReqDto updateInventoryReqDto);
}
