package co.in.sagarkale.airBnbClone.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HotelContactInfo {
    private String completeAddress;
    private String email;
    private String phoneNumber;
    private String location;
}
