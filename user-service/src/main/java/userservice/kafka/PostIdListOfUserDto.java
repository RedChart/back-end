package userservice.kafka;

import userservice.feign.dto.PostIdListDto;


public class PostIdListOfUserDto{
    private PostIdListDto postIdListDto;
    private Long userId;
    public PostIdListOfUserDto(PostIdListDto postIdListDto, Long userId) {
        this.postIdListDto = postIdListDto;
        this.userId = userId;
    }

}
