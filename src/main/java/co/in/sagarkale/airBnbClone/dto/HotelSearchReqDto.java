package co.in.sagarkale.airBnbClone.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HotelSearchReqDto {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomCount;
    private Integer pageNo = 0;
    private Integer pageSize = 10;
}
