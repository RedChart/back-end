package postservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import postservice.feign.dto.ServerUserDto;

import java.util.List;

// 정보를 받음
@FeignClient(name = "user-service", contextId = "user-service-to-post-service")
public interface UserServiceClientForPostService  {


    @GetMapping("/users/details/{userId}")
    ServerUserDto getUserById(@PathVariable("userId") Long userId);

    @PostMapping("/userList/details")
    List<ServerUserDto> getUserListDetailsByIdList(@RequestBody List<String> postIdList);
}
