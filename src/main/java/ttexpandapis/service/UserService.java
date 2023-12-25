package ttexpandapis.service;

import ttexpandapis.dto.CreateUserDto;
import ttexpandapis.dto.UserResponseDto;

public interface UserService {
    UserResponseDto register(CreateUserDto requestDto);
}
