package userservice.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowUserListDto {
    private Long user_id;
    private String username;
    private String profileImage;

}