package co.in.sagarkale.airBnbClone.security;

import co.in.sagarkale.airBnbClone.dto.LoginReqDto;
import co.in.sagarkale.airBnbClone.dto.SignupReqDto;
import co.in.sagarkale.airBnbClone.dto.UserDto;
import co.in.sagarkale.airBnbClone.entity.User;
import co.in.sagarkale.airBnbClone.entity.enums.Role;
import co.in.sagarkale.airBnbClone.exception.ResourceNotFoundException;
import co.in.sagarkale.airBnbClone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ModelMapper modelMapper;

    public UserDto signUp(SignupReqDto signupReqDto){
        User user = userRepository.findByEmail(signupReqDto.getEmail()).orElse(null);
        if(user != null){
          throw new RuntimeException("User already exist with email id: "+user.getEmail());
        }

        User newUser = modelMapper.map(signupReqDto, User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signupReqDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginReqDto loginReqDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginReqDto.getEmail(),
                        loginReqDto.getPassword()
                )
        );

        User user = (User) authentication.getPrincipal();

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String refreshToken(String refreshToken){
            Long userId = jwtService.getUserIdFromToken(refreshToken);

            User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found with id: "+userId));
            return jwtService.generateAccessToken(user);
    }

}
