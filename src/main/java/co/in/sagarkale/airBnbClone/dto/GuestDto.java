package co.in.sagarkale.airBnbClone.dto;

import co.in.sagarkale.airBnbClone.entity.User;
import co.in.sagarkale.airBnbClone.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private String name;
    private Integer age;
    private Gender gender;
}
