//package postservice.feign;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import postservice.service.PostService;
//import java.util.List;
//
//// 정보를 줌
//@RestController
//@RequestMapping("/posts")
//@RequiredArgsConstructor
//public class PostServiceClient {
//    private final PostService postService;
//
//    @PostMapping("/list/{userId}")
//    public List<ServerUserDto> getPostsById(@PathVariable Long userId, @RequestBody List<String> postIdList){
//        return postService.getPostsById(userId, postIdList);
//    }
////
////    @GetMapping("/details/{userId}")
////    public ServerPostDto getCreateDateAndIdById(@PathVariable("userId") Long userId){
////        return postService.getCreateDateAndIdById(userId);
////    }
//}
