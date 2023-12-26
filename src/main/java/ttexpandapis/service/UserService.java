package ttexpandapis.service;

import ttexpandapis.dto.CreateUserDto;
import ttexpandapis.dto.UserResponseDto;
import ttexpandapis.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(CreateUserDto requestDto) throws RegistrationException;
}
