package postservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import postservice.dto.request.RequestPostDto;
import postservice.dto.response.ResponsePostDto;
import postservice.service.PostService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "post 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/create")
    public ResponseEntity<?> creatpost(@Valid @RequestBody RequestPostDto request, @RequestHeader("userId") Long userId) {
        postService.createpost(request, userId);
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 생성되었습니다.");
    }

    @Operation(summary = "post 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PutMapping("/update/{post_id}")
    public ResponseEntity<?> updatepost(@Valid @RequestBody RequestPostDto request, @PathVariable("post_id") Long postId,@RequestHeader("userId") Long userId) {
        postService.updatepost(request, postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 수정되었습니다.");
    }
    @Operation(summary = "post 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/details/{post_id}")
    public ResponseEntity<?> detailspost(@PathVariable("post_id") Long postId,@RequestHeader("userId") Long userId) {
        ResponsePostDto responsePostDto = postService.detailspost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(responsePostDto);
    }

    @Operation(summary = "post 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping("/delete/{post_id}")
    public ResponseEntity<?> deletepost(@PathVariable("post_id") Long postId,@RequestHeader("userId") Long userId) {
        postService.deletepost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("게시물이 삭제되었습니다.");
    }

//    @PostMapping("/list/{userId}")
//    public List<ServerUserDto> getPostsById(@PathVariable Long userId, @RequestBody List<String> postIdList){
//        return postService.getPostsById(userId, postIdList);
//    }
}
