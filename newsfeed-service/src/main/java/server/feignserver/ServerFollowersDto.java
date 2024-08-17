package server.feignserver;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServerFollowersDto {
    private String username;
    private String profileImage;
}
