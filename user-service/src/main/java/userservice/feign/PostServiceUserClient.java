package userservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import userservice.feign.dto.PostIdListDto;

// 정보를 받음
@FeignClient(name = "post-service", contextId = "post-service-to-user-service")
public interface PostServiceUserClient  {


    @GetMapping("/posts/details/{userId}")
    PostIdListDto getCreateDateAndIdById(@PathVariable("userId") Long userId);
}
