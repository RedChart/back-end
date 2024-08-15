package server.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowDto {
        private Long user_id;
        private Long followee_id;
}
