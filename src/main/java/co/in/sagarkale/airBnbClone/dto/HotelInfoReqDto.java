package co.in.sagarkale.airBnbClone.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelInfoReqDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long roomsCount;
}
