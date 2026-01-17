package co.in.sagarkale.airBnbClone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelReportDto {
    private Long bookingCount;
    private BigDecimal revenue;
    private BigDecimal avgRevenue;
}
