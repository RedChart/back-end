package newsfeedservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import newsfeedservice.feign.dto.PostIdListDto;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostIdListOfUserDto{
    private PostCreateDateAndIdListDto postCreateDateAndIdListDto;
    private Long userId;

}
