package userservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import userservice.domain.User;

@Getter
@AllArgsConstructor
public class UserDto {
    private final String email;

    private final String username;

    private final String password;

    public UserDto(User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
}
