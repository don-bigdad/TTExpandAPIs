package ttexpandapis.controller;

import ttexpandapis.dto.CreateUserDto;
import ttexpandapis.dto.UserLoginRequestDto;
import ttexpandapis.dto.UserLoginResponseDto;
import ttexpandapis.dto.UserResponseDto;
import ttexpandapis.security.AuthenticationService;
import ttexpandapis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ttexpandapis.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/add")
    public UserResponseDto add(@RequestBody @Valid
                                   CreateUserDto requestDto) {
        return userService.register(requestDto);
    }

    @PostMapping(value = "/authenticate")
    public UserLoginResponseDto login(@RequestBody @Valid
                                          UserLoginRequestDto requestDto) {
        return authenticationService.authenticateUser(requestDto);
    }
}
