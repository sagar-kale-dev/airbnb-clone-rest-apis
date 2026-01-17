package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.RoomDto;

import java.util.List;

public interface RoomService {
    RoomDto createNewRoom(Long hotelId, RoomDto roomDto);
    List<RoomDto> getAllRoomsInHotel(Long hotelId);
    RoomDto getRoomDetailsById(Long roomId);
    void deleteRoomById(Long roomId);
    RoomDto updateRoomDetails(Long hotelId, Long roomId, RoomDto roomDto);
}
