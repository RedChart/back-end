package userservice.feign;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import userservice.feign.dto.ClientUserDto;
import userservice.feign.dto.FollowersListDto;
import userservice.service.FollowService;
import userservice.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserServiceClient {
    private final UserService userService;
    private final FollowService followService;
    @GetMapping("/details/{userId}")
    public ClientUserDto getUserById(@PathVariable Long userId){
        ClientUserDto user = userService.getUserById(userId);
        return user;
    }

    @GetMapping("/followers/{userId}")
    public FollowersListDto getFollowersById(@PathVariable Long userId){
        FollowersListDto followersId = followService.getfollowersId(userId);
        return followersId;
    }

//    @GetMapping("/userList/details")
//    public List<ServerUserDto> getUserListDetailsByIdList(@RequestBody List<String> postIdList){
//        FollowersListDto followersId = followService.getfollowersId(userId);
//        return followersId;
//    }
}
