package newsfeedservice.feign;

import newsfeedservice.feign.dto.ClientUserDto;
import newsfeedservice.feign.dto.FollowersListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", contextId = "user-service-to-newsfeed-service")
public interface UserServiceClient  {

    @GetMapping("/users/followers/{userId}")
    FollowersListDto getFollowersById(@PathVariable("userId") Long userId);

    @GetMapping("/users/detail/{userId}")
    ClientUserDto getUserById(@PathVariable("userId") Long userId);

}
