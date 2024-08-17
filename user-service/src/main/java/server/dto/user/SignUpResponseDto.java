package server.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.domain.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponseDto {
    private String password;
    private String username;


    public static SignUpResponseDto toDto(User user) {
        return new SignUpResponseDto(user.getPassword(), user.getUsername());
    }
}
