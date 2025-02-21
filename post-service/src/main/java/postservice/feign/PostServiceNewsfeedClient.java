package postservice.feign;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import postservice.feign.dto.ServerUserDto;
import postservice.service.PostService;

import java.util.List;

// 정보를 줌
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostServiceNewsfeedClient {
    private final PostService postService;

    @PostMapping("/list/{userId}")
    public List<ServerUserDto> getPostsById(@PathVariable Long userId, @RequestBody List<String> postIdList){
        return postService.getPostsById(userId, postIdList);
    }
}

