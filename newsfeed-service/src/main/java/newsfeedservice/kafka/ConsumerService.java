package newsfeedservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
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
//import newsfeedservice.kafka.PostEventDto;

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

    //    private final RedisTemplate<String, ArrayList<String>> redisTemplate;
    private final RedisTemplate<String, List<String>> redisTemplate;
//    @Autowired
//    public ConsumerService(ServerController serverController, RedisTemplate<String, String> redisTemplate) {
//        this.serverController = serverController;
//        this.redisTemplate = redisTemplate;
//    }


    @KafkaListener(topics = "post-event", groupId = "newsfeed-service-group", containerFactory = "kafkaListenerContainerFactory")//확인 필요
    public void listenPostEvent(Object postEventDto) {
//    public void listenPostEvent(ConsumerRecord<String, PostEventDto> record) {
//        PostEventDto postEventDto = record.value();
//    public void listenPostEvent(@Payload PostEventDto postEventDto){
        log.info(postEventDto.toString());

//        FollowersListDto followersList= userServiceClient.getFollowersById(postEventDto.getPostWriterId());
//        String redisValue = postEventDto.getPostId()+":"+postEventDto.getCreateDate();
//        List<String> myCurrentList = redisTemplate.opsForValue().get(postEventDto.getPostWriterId().toString());
//        log.info("-----------------------------------------------------");
////        if (myCurrentList == null) {
////            myCurrentList = new ArrayList<>();
////        }
//        // JSON 데이터를 AData 객체로 변환
//        switch (postEventDto.getType()) {
//            // feign client로 userId의 follower를 받음
//            case "CREATED":
//                //follower api로 받음
//                log.info("-----------------------------------------------------");
//                followersList.getFollowersList().stream()
//                        .map(Object::toString)  // Long 타입의 ID를 문자열로 변환하여 키로 사용
//                        .forEach(followerId -> {
//                            List<String> currentList = redisTemplate.opsForValue().get(followerId);
//                            currentList.add(redisValue);
//                            redisTemplate.opsForValue().set(followerId, currentList);
//                            }
//                        );
//
//                myCurrentList.add(redisValue);
//                redisTemplate.opsForValue().set(postEventDto.getPostWriterId().toString(), myCurrentList);
////
////                redisTemplate.opsForValue().get("2").stream().forEach(a->log.info(a));
////                redisTemplate.opsForValue().get("3").stream().forEach(a->log.info(a));
//                // key == follow Id 인 redis에 postId를 넣음
//                break;
//            case "DELETED":
//                followersList.getFollowersList().stream()
//                    .map(Object::toString)  // Long 타입의 ID를 문자열로 변환하여 키로 사용
//                    .forEach(followerId ->                {
//                        List<String> currentList = redisTemplate.opsForValue().get(followerId);
//                        if (currentList != null && currentList.contains(redisValue)) {
//                            currentList.remove(redisValue);
//
//                            // 업데이트된 리스트를 Redis에 다시 저장
//                            redisTemplate.opsForValue().set(followerId, currentList);
//                        }
//                    });
//
//                myCurrentList.add(redisValue);
//                redisTemplate.opsForValue().set(postEventDto.getPostWriterId().toString(), myCurrentList);
//                // key == follow Id 인 redis에 postId를 지음
//                break;
//        }
    }

    @KafkaListener(topics = "user-event", groupId = "newsfeed-service-group")
    public void listenUserEvent(String userId) {
        // redis에 key:userId, value=[] 생성
        log.info(userId);
        String key = userId;
        List<String> emptyList = new ArrayList<>();  // 빈 리스트 생성
        redisTemplate.opsForValue().set(key, emptyList);
    }

    @KafkaListener(topics = "add-follow-topic", groupId = "newsfeed-service-group", containerFactory = "kafkaListenerContainerFactory")//확인 필요
    public void listenAddFolloewEvent(ConsumerRecord<String, PostIdListOfUserDto> record) {
        PostIdListOfUserDto postIdListOfUserDto = record.value();
        //아니지 지금 저장되
        List<String> existingPostIds = redisTemplate.opsForValue().get(postIdListOfUserDto.getUserId().toString());
        // 3️⃣ 기존 + 새로운 리스트 병합 후, 숫자로 변환하여 정렬 (내림차순)
        List<String> sortedPostIds = existingPostIds.stream()
                .map(Object::toString) // 기존 데이터 숫자로 변환
                .collect(Collectors.toList()); // 리스트로 변환

        sortedPostIds.addAll(postIdListOfUserDto.getPostIdListDto().getPostIdList().stream()
                .map(Object::toString) // 새로운 데이터 숫자로 변환
                .collect(Collectors.toList()));

        sortedPostIds = sortedPostIds.stream()
                .sorted(Comparator.reverseOrder()) // 내림차순 정렬
                .map(String::valueOf) // 다시 String으로 변환
                .collect(Collectors.toList());

        // 4️⃣ 정렬된 리스트를 Redis에 저장
        redisTemplate.opsForValue().set(postIdListOfUserDto.getUserId().toString(), sortedPostIds);
    }

}
