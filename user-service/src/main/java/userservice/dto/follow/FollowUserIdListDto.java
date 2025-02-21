package userservice.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowUserIdListDto {
    private Long user_id;
    private List<Long> follower_id;

}