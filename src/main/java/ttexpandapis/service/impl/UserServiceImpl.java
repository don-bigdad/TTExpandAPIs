package ttexpandapis.service.impl;

import ttexpandapis.dto.CreateUserDto;
import ttexpandapis.dto.UserResponseDto;
import ttexpandapis.entity.User;
import ttexpandapis.mapper.UserMapper;
import ttexpandapis.repository.UserRepository;
import ttexpandapis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(CreateUserDto requestDto) {
        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }
}
