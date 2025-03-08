package postservice.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import postservice.feign.dto.PostCreateDateAndIdListDto;
import postservice.feign.dto.PostIdListDto;
import postservice.service.PostService;

// 정보를 줌
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostServiceUserClient {
    private final PostService postService;

    @GetMapping("/details/{userId}")
    public PostCreateDateAndIdListDto getPostsById(@PathVariable Long userId){
        return postService.getPostCreateDateAndIdListById(userId);
    }
}

