package userservice.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CountFollowDto {
    Long following;
    Long follower;
}
