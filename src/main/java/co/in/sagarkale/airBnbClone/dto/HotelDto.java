package co.in.sagarkale.airBnbClone.dto;

import co.in.sagarkale.airBnbClone.entity.HotelContactInfo;
import lombok.Data;

@Data
public class HotelDto {
    private Long id;
    private String city;
    private String name;
    private HotelContactInfo contactInfo;
    private String[] photos;
    private String[] amenities;
    private Boolean active;
}
