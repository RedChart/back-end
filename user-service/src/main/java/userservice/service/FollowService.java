package userservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import userservice.feign.PostCreateDateAndIdListDto;
import userservice.kafka.PostIdListOfUserDto;
import userservice.domain.Follow;
import userservice.domain.User;
import userservice.dto.follow.CountFollowDto;
import userservice.dto.follow.FollowUserListDto;
import userservice.feign.dto.FollowersListDto;
import userservice.feign.PostServiceUserClient;
import userservice.repository.FollowRepository;
import userservice.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final PostServiceUserClient postServiceUserClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void addfollow(Long userId, Long followeeId) {
        User follower = userRepository.findById(followeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다" + followeeId));
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다" + userId));
        if (!followRepository.existsByFollowingAndFollower(following, follower)){
            Follow follow = new Follow(following, follower);
            followRepository.save(follow);

            // redis db에서 post id를 추가
            // 1. followeeId의 최근 게시물의 createDateAndId 가져오기 (feign client로)
            PostCreateDateAndIdListDto postCreateDateAndIdListDto =  postServiceUserClient.getCreateDateAndIdById(followeeId);
            // 2. 카프카로 userId + PostCreateDateAndIdList를 newsfeed에 전달

            log.info(postCreateDateAndIdListDto.getPostCreateDateAndIdList().toString());
            kafkaTemplate.send("add-follow-topic", new PostIdListOfUserDto(postCreateDateAndIdListDto, userId));


        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    @Transactional
    public void deletefollow(Long userId, Long followeeId) {
        User follower = userRepository.findById(followeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다" + followeeId));
        User following = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "유저가 존재하지 않습니다" + userId));
        if (followRepository.existsByFollowingAndFollower(following, follower)){
            followRepository.findByFollowerAndFollowing(follower, following);
            followRepository.deleteByFollowerAndFollowing(follower, following);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    // 리스트 가져오기
    public List<FollowUserListDto> getfollowings(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
        return follows.stream()
                .map(follow -> new FollowUserListDto(follow.getFollowing().getId(), follow.getFollowing().getUsername(),follow.getFollowing().getProfileImage()))
                .collect(Collectors.toList());
    }
    public List<FollowUserListDto> getfollowers(Long userId) {
        List<Follow> follows = followRepository.findByFollowingId(userId);
        return follows.stream()
                .map(follow -> new FollowUserListDto(follow.getFollower().getId(), follow.getFollower().getUsername(),follow.getFollower().getProfileImage()))
                .collect(Collectors.toList());
    }
    public FollowersListDto getfollowersId(Long userId) {
        List<Follow> follows = followRepository.findByFollowerId(userId);
//        List<Long> followersList= follows.stream()
//                .map(follow ->  follow.getFollower().getId())
//                .collect(Collectors.toList());
        List<Long> followersList= follows.stream()
                .map(follow ->  follow.getFollowing().getId())
                .collect(Collectors.toList());
        log.info(followersList.toString());
        return new FollowersListDto(followersList);
    }

    public CountFollowDto countFollow(Long userId) {
        Long follower = followRepository.countByFollowerId(userId);
        Long following = followRepository.countByFollowingId(userId);
        return new CountFollowDto(following, follower);
    }

}