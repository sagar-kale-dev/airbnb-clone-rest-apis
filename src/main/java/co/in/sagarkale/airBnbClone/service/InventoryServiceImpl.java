package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.*;
import co.in.sagarkale.airBnbClone.entity.*;
import co.in.sagarkale.airBnbClone.exception.ResourceNotFoundException;
import co.in.sagarkale.airBnbClone.exception.UnAuthorizedException;
import co.in.sagarkale.airBnbClone.repository.HotelMinPriceRepository;
import co.in.sagarkale.airBnbClone.repository.InventoryRepository;
import co.in.sagarkale.airBnbClone.repository.RoomRepository;
import co.in.sagarkale.airBnbClone.util.AppUtils;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        for (; !today.isAfter(endDate); today = today.plusDays(1)){
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .date(today)
                    .price(room.getBasePrice())
                    .bookedCount(0)
                    .totalCount(room.getTotalCount())
                    .surgeFactor(BigDecimal.ONE)
                    .closed(false)
                    .city(room.getHotel().getCity())
                    .build();
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public void deleteFutureInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchReqDto hotelSearchReqDto) {
        Pageable pageable = PageRequest.of(
                hotelSearchReqDto.getPageNo(),
                hotelSearchReqDto.getPageSize()
        );
        Long dateCount = ChronoUnit.DAYS.between(
                hotelSearchReqDto.getStartDate(),
                hotelSearchReqDto.getEndDate()
        ) + 1;

        // Schedular will keep the price updated for next 90 days. So user search for check-in date
        // greater than 90 days then give search results based on inventory
        Page<HotelPriceDto> hotelPage;
//        if(hotelSearchReqDto.getStartDate()
//                .isBefore(LocalDate.now().plusDays(90))){
            hotelPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                    hotelSearchReqDto.getCity(),
                    hotelSearchReqDto.getStartDate(),
                    hotelSearchReqDto.getEndDate(),
                    pageable
            );
//        }else{
//            hotelPage = inventoryRepository.findHotelsWithAvailableInventory(
//                    hotelSearchReqDto.getCity(),
//                    hotelSearchReqDto.getStartDate(),
//                    hotelSearchReqDto.getEndDate(),
//                    hotelSearchReqDto.getRoomCount(),
//                    dateCount,
//                    pageable
//            );
//        }

        log.info("Hotel search : {}",hotelPage);
        return hotelPage;
    }

    @Override
    public List<InventoryDto> getAllInventoryByRoomId(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with id "+roomId));

        User user = AppUtils.getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorizedException("This user does not own the room with id: "+roomId);
        }

        List<Inventory> inventoryList = inventoryRepository.findByRoomOrderByDate(room);
        return inventoryList.stream()
                .map((element) -> modelMapper.map(element, InventoryDto.class)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryReqDto updateInventoryReqDto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with id "+roomId));

        User user = AppUtils.getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorizedException("This user does not own the room with id: "+roomId);
        }

        inventoryRepository.findInventoryAndLockBeforeUpdate(
                roomId,
                updateInventoryReqDto.getStartDate(),
                updateInventoryReqDto.getEndDate()
        );

        inventoryRepository.updateInventory(
                roomId,
                updateInventoryReqDto.getStartDate(),
                updateInventoryReqDto.getEndDate(),
                updateInventoryReqDto.getSurgeFactor(),
                updateInventoryReqDto.getClose()
        );
    }

}
