package newsfeedservice.dto;

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

}
