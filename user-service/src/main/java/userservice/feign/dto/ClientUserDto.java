package userservice.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import userservice.domain.User;

@Getter
@AllArgsConstructor
public class ClientUserDto {
    private Long userId;
    private String username;
    private String profileImage;
    public ClientUserDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.profileImage = user.getProfileImage();
    }
}
