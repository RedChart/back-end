package postservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import postservice.service.LikesService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/likes")
public class LikesController {

    private final LikesService likesService;

    @Operation(summary = "좋아요 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/{post_id}/{user_id}/add")
    public ResponseEntity<?> addlike(@PathVariable("post_id") Long postId,@RequestHeader("userId") Long userId) {
        likesService.addlike(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(postId+"에 좋아요를 눌렀습니다");
    }

    @Operation(summary = "좋아요 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PutMapping("/{post_id}/{user_id}/delete")
    public ResponseEntity<?> deletelike(@PathVariable("post_id") Long postId,@RequestHeader("userId") Long userId) {
        likesService.deletelike(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(postId+"에 좋아요를 삭제했습니다");
    }
}