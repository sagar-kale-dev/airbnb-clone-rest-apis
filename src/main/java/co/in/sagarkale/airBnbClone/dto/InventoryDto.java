package co.in.sagarkale.airBnbClone.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InventoryDto {
    private Long id;
    private LocalDate date;
    private BigDecimal price;
    private Integer bookedCount;
    private Integer reservedCount;
    private Integer totalCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal surgeFactor;
    private Boolean closed;
    private String city;
}
