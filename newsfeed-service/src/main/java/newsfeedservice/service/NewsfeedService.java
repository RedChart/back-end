package newsfeedservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import newsfeedservice.dto.NewsfeedDTO;
import newsfeedservice.feign.PostServiceNewsfeedClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsfeedService {

    private PostServiceNewsfeedClient postServiceNewsfeedClient;
    private final RedisTemplate<String, List<String>> redisTemplate;
    public NewsfeedDTO getNewsfeedDetails(Long userId) {
        // redis에서 userId의 postId리스트를 꺼내옴
        List<String> postIdList = redisTemplate.opsForValue().get(userId.toString());
        // post에 api 요청
        return postServiceNewsfeedClient.getPostsById(userId, postIdList);
    }
}