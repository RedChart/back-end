package postservice.dto.response;

import lombok.Builder;
import postservice.domain.Post;

@Builder
public class NewsfeedDTO {
    // user 관련 정보
    private Long writerId;
    private String writername;
    private boolean isWriter;
    private String profileImage;

    // post 관련 정보
    private String title;
    private String content;
    private String commentCnt;
    private String likeCnt;
    private String isLike;

    private NewsfeedDTO convertToNewsfeedDTO(Post post) {
        // DTO 생성 및 반환
        return NewsfeedDTO.builder()
                .writerId(post.getWriterId())
                .writername("Dummy Writer Name") // 적절히 설정
                .isWriter(true) // 임의 로직으로 설정
                .profileImage("default-profile.png") // 기본값 설정
                .title(post.getTitle())
                .content(post.getContent())
                .commentCnt("0") // 기본값
                .likeCnt("0") // 기본값
                .isLike("false") // 기본값
                .build();
    }
}
