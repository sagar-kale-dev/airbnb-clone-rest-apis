package co.in.sagarkale.airBnbClone.controller;

import co.in.sagarkale.airBnbClone.dto.*;
import co.in.sagarkale.airBnbClone.service.HotelService;
import co.in.sagarkale.airBnbClone.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
@Tag(name = "User -  Hotels", description = "User level hotels endpoints")
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @PostMapping("/search")
    @Operation(summary = "Search hotels")
    public ResponseEntity<Page<HotelPriceDto>> searchHotel(@RequestBody HotelSearchReqDto searchReqDto){
        Page<HotelPriceDto> page = inventoryService.searchHotels(searchReqDto);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/{hotelId}/info")
    @Operation(summary = "Get hotel info for user")
    public ResponseEntity<HotelInfoDto> getHotelInfo(
            @PathVariable Long hotelId,
            @RequestBody HotelInfoReqDto hotelInfoReqDto
    ){
        return ResponseEntity.ok(hotelService.getHotelInfo(hotelId, hotelInfoReqDto));
    }

}
