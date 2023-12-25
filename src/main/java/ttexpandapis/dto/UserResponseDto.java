package ttexpandapis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String username;
    private String password;
}
