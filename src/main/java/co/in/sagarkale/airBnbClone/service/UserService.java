package co.in.sagarkale.airBnbClone.service;

import co.in.sagarkale.airBnbClone.dto.ProfileUpdateRequestDto;
import co.in.sagarkale.airBnbClone.dto.UserDto;
import co.in.sagarkale.airBnbClone.entity.User;

public interface UserService {
    User getUserById(Long id);
    void updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto);
    UserDto getMyProfile();
}
