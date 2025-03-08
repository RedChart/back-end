package userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// 정보를 받음
@FeignClient(name = "post-service", contextId = "post-service-to-user-service")
public interface PostServiceUserClient  {


    @GetMapping("/posts/details/{userId}")
    PostCreateDateAndIdListDto getCreateDateAndIdById(@PathVariable("userId") Long userId);
}
