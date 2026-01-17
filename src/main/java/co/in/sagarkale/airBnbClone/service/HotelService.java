package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.BookingDto;
import co.in.sagarkale.airBnbClone.dto.HotelDto;
import co.in.sagarkale.airBnbClone.dto.HotelInfoDto;
import co.in.sagarkale.airBnbClone.dto.HotelInfoReqDto;

import java.util.List;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelDetails(Long hotelId);
    List<HotelDto> getAllHotels();
    HotelDto updateHotel(Long hotelId, HotelDto hotelDto);
    void deleteHotel(Long hotelId);
    void activateHotel(Long hotelId);
    HotelInfoDto getHotelInfo(Long hotelId, HotelInfoReqDto hotelInfoReqDto);
}
