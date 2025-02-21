package postservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import postservice.domain.Post;


@Getter
@NoArgsConstructor
public class RequestPostDto {


    @Size(max = 30, message = "제목은 최대 30글자 입니다.")
    @NotNull(message = "제목을 입력해주세요.")
    private String title;

    @Size(max = 100, message = "내용은 최대 30글자 입니다.")
    @NotNull(message = "내용을 입력해주세요.")
    private String content;

    public RequestPostDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post toEntity(Long userId) {
        return Post.builder()
                .title(title)
                .writerId(userId)
                .content(content)
                .build();
    }
}
