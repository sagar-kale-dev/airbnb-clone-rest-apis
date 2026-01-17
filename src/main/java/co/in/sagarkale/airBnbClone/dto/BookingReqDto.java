package co.in.sagarkale.airBnbClone.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingReqDto {
    private Long hotelId;
    private Long roomId;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
