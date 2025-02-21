package newsfeedservice.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import newsfeedservice.feign.dto.PostIdListDto;

@NoArgsConstructor
@Getter
public class PostIdListOfUserDto{
    private PostIdListDto postIdListDto;
    private Long userId;

}
