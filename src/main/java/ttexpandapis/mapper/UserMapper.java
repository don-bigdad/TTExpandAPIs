package ttexpandapis.mapper;


import ttexpandapis.dto.CreateUserDto;
import ttexpandapis.dto.UserResponseDto;
import ttexpandapis.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(CreateUserDto userDto) {
        return new User()
                .setUsername(userDto.username())
                .setPassword(userDto.password());
    }

    public UserResponseDto toDto(User user) {
        return new UserResponseDto()
                .setId(user.getId())
                .setPassword(user.getPassword())
                .setUsername(user.getUsername());
    }
}
