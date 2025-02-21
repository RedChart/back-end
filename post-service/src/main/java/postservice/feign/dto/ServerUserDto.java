package postservice.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerUserDto {
    private String username;
    private String profileImage;

}
