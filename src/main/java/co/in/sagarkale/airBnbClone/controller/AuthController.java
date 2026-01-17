package co.in.sagarkale.airBnbClone.controller;

import co.in.sagarkale.airBnbClone.dto.LoginReqDto;
import co.in.sagarkale.airBnbClone.dto.LoginResponseDto;
import co.in.sagarkale.airBnbClone.dto.SignupReqDto;
import co.in.sagarkale.airBnbClone.dto.UserDto;
import co.in.sagarkale.airBnbClone.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth related APIs")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Signup user")
    public ResponseEntity<UserDto> signup(@RequestBody SignupReqDto signupReqDto){
        return new ResponseEntity<>(authService.signUp(signupReqDto), HttpStatus.CREATED);
    }

//    Refresh token will be set in cookies after login
//    @PostMapping("/login")
//    @Operation(summary = "Login user")
//    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginReqDto loginReqDto, HttpServletResponse httpServletResponse){
//        String[] arrToken = authService.login(loginReqDto);
//
//        Cookie cookie = new Cookie("refreshToken", arrToken[1]);
//        cookie.setHttpOnly(true);
//
//        httpServletResponse.addCookie(cookie);
//        return ResponseEntity.ok(new LoginResponseDto(arrToken[0]));
//    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginReqDto loginReqDto, HttpServletResponse httpServletResponse){
        String[] arrToken = authService.login(loginReqDto);
        return ResponseEntity.ok(new LoginResponseDto(arrToken[0], arrToken[1]));
    }

//    Refresh token will be parsed from cookies while refresh token request
//    @GetMapping("/refresh")
//    @Operation(summary = "Refresh token")
//    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest httpServletRequest){
//        String refreshToken = Arrays.stream(httpServletRequest.getCookies())
//                .filter(cookie -> cookie.getName().equals("refreshToken"))
//                .findFirst()
//                .map(Cookie::getValue)
//                .orElseThrow(()->new AuthenticationServiceException("Refresh token not found in cookies"));
//
//        String accessToken = authService.refreshToken(refreshToken);
//
//        return ResponseEntity.ok(new LoginResponseDto(accessToken));
//    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody String refreshToken, HttpServletRequest httpServletRequest){
        String accessToken = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

}
