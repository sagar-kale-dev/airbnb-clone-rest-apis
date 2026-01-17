package co.in.sagarkale.airBnbClone.controller;

import co.in.sagarkale.airBnbClone.dto.RoomDto;
import co.in.sagarkale.airBnbClone.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Admin level endpoints for room")
public class RoomAdminController {
    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create new room for hotel")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long hotelId, @RequestBody RoomDto roomDto){
        RoomDto room = roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all rooms in hotel")
    public ResponseEntity<List<RoomDto>> getAllRoomsByHotelId(@PathVariable Long hotelId){
        List<RoomDto> rooms= roomService.getAllRoomsInHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "Get particular room details by room id")
    public ResponseEntity<RoomDto> getRoomDetails(@PathVariable Long roomId){
        RoomDto roomDto = roomService.getRoomDetailsById(roomId);
        return ResponseEntity.ok(roomDto);
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "Delete room by room id")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{roomId}")
    @Operation(summary = "Update room details by room id")
    public ResponseEntity<RoomDto> updateRoomDetails(
            @PathVariable Long hotelId,
            @PathVariable Long roomId,
            @RequestBody RoomDto roomDto
    ){
        return ResponseEntity.ok(roomService.updateRoomDetails(hotelId, roomId, roomDto));
    }
}
