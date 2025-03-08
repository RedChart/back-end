package newsfeedservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import newsfeedservice.domain.Post;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import newsfeedservice.feign.dto.FollowersListDto;
import newsfeedservice.feign.UserServiceClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, List<String>> redisTemplate;
    private final RedisTemplate<String, String> redisList;

    @KafkaListener(topics = "post-event", groupId = "newsfeed-service-group", containerFactory = "kafkaListenerContainerFactory")//확인 필요
    public void listenPostEvent(ConsumerRecord<String, Object> record) {
        objectMapper.registerModule(new JavaTimeModule());
        // record.value()가 LinkedHashMap이므로 PostEventDto로 변환
        PostEventDto postEventDto = objectMapper.convertValue(record.value(), PostEventDto.class);
        FollowersListDto followersList= userServiceClient.getFollowersById(postEventDto.getPostWriterId());
        String redisValue = postEventDto.getPostCreateDateAndId();
        List<String> myCurrentList = redisTemplate.opsForValue().get(postEventDto.getPostWriterId().toString());
        switch (postEventDto.getType()) {
            // feign client로 userId의 follower를 받음
            case "CREATED":
                //follower api로 받음
                followersList.getFollowersList().stream()
                        .map(Object::toString)  // Long 타입의 ID를 문자열로 변환하여 키로 사용
                        .forEach(followerId -> {
                            List<String> currentList = redisTemplate.opsForValue().get(followerId);
                            currentList.add(0,redisValue);
                            redisTemplate.opsForValue().set(followerId, currentList);

                            redisList.opsForList().leftPush(followerId+"list","postEventDto.getPostCreateDateAndId()");
                            }
                        );
                redisList.opsForList().leftPush(postEventDto.getPostWriterId()+"list",postEventDto.getPostCreateDateAndId());
                myCurrentList.add(0,redisValue);
                redisTemplate.opsForValue().set(postEventDto.getPostWriterId().toString(), myCurrentList);
                // key == follow Id 인 redis에 postId를 넣음
                break;
            case "DELETED":
                followersList.getFollowersList().stream()
                    .map(Object::toString)  // Long 타입의 ID를 문자열로 변환하여 키로 사용
                    .forEach(followerId ->                {
                        List<String> currentList = redisTemplate.opsForValue().get(followerId);
                        if (currentList != null && currentList.contains(redisValue)) {
                            currentList.remove(redisValue);

                            // 업데이트된 리스트를 Redis에 다시 저장
                            redisTemplate.opsForValue().set(followerId, currentList);
                        }
                    });
                redisList.opsForList().remove(postEventDto.getPostWriterId()+"list",0,postEventDto.getPostCreateDateAndId());
//                assert myCurrentList != null;
//                myCurrentList.remove(redisValue);
//                redisTemplate.opsForValue().set(postEventDto.getPostWriterId().toString(), myCurrentList);
                // key == follow Id 인 redis에 postId를 지음
                break;
        }
    }

    @KafkaListener(topics = "user-event", groupId = "newsfeed-service-group")
    public void listenUserEvent(String userId) {
        // redis에 key:userId, value=[] 생성
        String key = userId;
        List<String> emptyList = new ArrayList<>();
        redisTemplate.opsForValue().set(key, emptyList);
    }

    @KafkaListener(topics = "add-follow-topic", groupId = "newsfeed-service-group", containerFactory = "kafkaListenerContainerFactory")//확인 필요
    public void listenAddFolloewEvent(ConsumerRecord<String, Object> record) {
        // record.value()가 LinkedHashMap이므로 PostEventDto로 변환
        PostIdListOfUserDto postIdListOfUserDto = objectMapper.convertValue(record.value(), PostIdListOfUserDto.class);
        List<String> existingPostIds = redisTemplate.opsForValue().get(postIdListOfUserDto.getUserId().toString());
        // 3️⃣ 기존 + 새로운 리스트 병합 후, 숫자로 변환하여 정렬 (내림차순)
        List<String> sortedPostIds = existingPostIds.stream()
                .map(Object::toString) // 기존 데이터 숫자로 변환
                .collect(Collectors.toList()); // 리스트로 변환
        sortedPostIds.addAll(postIdListOfUserDto.getPostCreateDateAndIdListDto().getPostCreateDateAndIdList().stream()
                .map(Object::toString) // 새로운 데이터 숫자로 변환
                .toList());
        sortedPostIds = sortedPostIds.stream()
                .sorted(Comparator.reverseOrder()) // 내림차순 정렬
                .map(String::valueOf) // 다시 String으로 변환
                .collect(Collectors.toList());
        // 4️⃣ 정렬된 리스트를 Redis에 저장
        redisTemplate.opsForValue().set(postIdListOfUserDto.getUserId().toString(), sortedPostIds);
    }

    @KafkaListener(topics = "delete-follow-topic", groupId = "newsfeed-service-group", containerFactory = "kafkaListenerContainerFactory")//확인 필요
    public void listenDeleteFolloewEvent(ConsumerRecord<String, Object> record) {
        PostIdListOfUserDto postIdListOfUserDto = objectMapper.convertValue(record.value(), PostIdListOfUserDto.class);
        String userId = postIdListOfUserDto.getUserId().toString();
// 1️⃣ 기존 뉴스피드 리스트 가져오기
        List<String> existingPostIds = redisTemplate.opsForValue().get(userId);
// 2️⃣ 삭제할 포스트 ID 리스트 추출
        List<String> removePostIds = postIdListOfUserDto.getPostCreateDateAndIdListDto()
                .getPostCreateDateAndIdList()
                .stream()
                .map(Object::toString) // 삭제할 데이터 숫자로 변환
                .toList();
        if (existingPostIds != null) {
            // 3️⃣ 기존 리스트에서 해당 포스트 ID 제거
            List<String> updatedPostIds = existingPostIds.stream()
                    .filter(postId -> !removePostIds.contains(postId)) // 삭제할 ID 제외
                    .sorted(Comparator.reverseOrder()) // 내림차순 정렬
                    .collect(Collectors.toList());
            // 4️⃣ 정렬된 리스트를 Redis에 저장
            redisTemplate.opsForValue().set(userId, updatedPostIds);
        }
    }
}
