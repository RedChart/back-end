package postservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import postservice.domain.Comment;
import postservice.domain.Post;

@Getter
@NoArgsConstructor
public class RequestUpdateCommentDto {


    @Size(max = 100, message = "내용은 최대 30글자 입니다.")
    @NotNull(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "comment에 해당하는 게시물을 찾을 수 없습니다.")
    private Long postId;


    public Comment toEntity(Post post, Long userId) {
        return Comment.builder()
                .writerId(userId)
                .content(content)
                .post(post)
                .build();
    }
}
