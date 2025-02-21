package newsfeedservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import newsfeedservice.dto.NewsfeedDTO;

import java.util.List;

@FeignClient(name = "post-service", contextId = "post-service-to-newsfeed-service")
public interface PostServiceNewsfeedClient {

//    @GetMapping("/posts/details/all")
//    NewsfeedDTO getFollowersById(@PathVariable List<Long> requestDto);

    // userId를 받는 것이 아니라 userId의 postId를 줘야힘
    @PostMapping("/posts/list/{userId}}")
    NewsfeedDTO getPostsById(@PathVariable Long userId, @RequestBody List<String> postIdList);

}
