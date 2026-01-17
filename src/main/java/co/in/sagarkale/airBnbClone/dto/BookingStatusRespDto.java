package co.in.sagarkale.airBnbClone.dto;

import co.in.sagarkale.airBnbClone.entity.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusRespDto {
    private BookingStatus bookingStatus;
}
