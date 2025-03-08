package userservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import userservice.feign.PostCreateDateAndIdListDto;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostIdListOfUserDto{
    private PostCreateDateAndIdListDto postCreateDateAndIdListDto;
    private Long userId;

}
