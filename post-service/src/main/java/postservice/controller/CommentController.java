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
import postservice.dto.request.RequestCreateCommentDto;
import postservice.dto.request.RequestUpdateCommentDto;
import postservice.service.CommentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/comments")
public class CommentController {

    private final CommentService commentService;
    @Operation(summary = "댓글 작성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/{post_id}/add")
    public ResponseEntity<?> addcomment(@Valid @RequestBody RequestCreateCommentDto request, @PathVariable("post_id") Long postId, @RequestHeader("userId") Long userId) {
        commentService.addcomment(request, postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 생성되었습니다.");
    }

    @Operation(summary = "댓글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PutMapping("/{post_id}/{comment_id}/delete")
    public ResponseEntity<?> updatecomment(@Valid @RequestBody RequestUpdateCommentDto request, @PathVariable("post_id") Long postId, @PathVariable("comment_id") Long commentId, @RequestHeader("userId") Long userId) {
        commentService.updatecomment(postId, userId, commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 수정되었습니다.");
    }

    @Operation(summary = "댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PutMapping("/{post_id}/{comment_id}/update")
    public ResponseEntity<?> deletecomment(@PathVariable("post_id") Long postId,@PathVariable("comment_id") Long commentId,@RequestHeader("userId") Long userId) {
        commentService.deletecomment(postId, userId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
    }
}


