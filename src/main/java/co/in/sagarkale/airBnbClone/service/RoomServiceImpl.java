package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.RoomDto;
import co.in.sagarkale.airBnbClone.entity.Hotel;
import co.in.sagarkale.airBnbClone.entity.Room;
import co.in.sagarkale.airBnbClone.entity.User;
import co.in.sagarkale.airBnbClone.exception.ResourceNotFoundException;
import co.in.sagarkale.airBnbClone.exception.UnAuthorizedException;
import co.in.sagarkale.airBnbClone.repository.HotelRepository;
import co.in.sagarkale.airBnbClone.repository.RoomRepository;
import co.in.sagarkale.airBnbClone.util.AppUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id "+hotelId));

        User user = AppUtils.getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("This user does not own the hotel with id: "+hotelId);
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id "+hotelId));

        User user = AppUtils.getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("This user does not own the hotel with id: "+hotelId);
        }

        return hotel.getRooms().stream()
                .map((element) -> modelMapper.map(element, RoomDto.class)).toList();
    }

    @Override
    public RoomDto getRoomDetailsById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with id "+roomId));
        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with id "+roomId));

        User user = AppUtils.getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorizedException("This user does not own the room with id: "+roomId);
        }

        inventoryService.deleteFutureInventories(room);
        roomRepository.deleteById(roomId);
    }

    @Override
    public RoomDto updateRoomDetails(Long hotelId, Long roomId, RoomDto roomDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id "+hotelId));

        User user = AppUtils.getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("This user does not an owner of hotel with id: "+hotelId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found with id "+roomId));

        modelMapper.map(roomDto, room);
        room.setId(roomId);

        //TODO: if price of the room updated then update the price of inventory as well
        room = roomRepository.save(room);

        return modelMapper.map(room, RoomDto.class);
    }

}
