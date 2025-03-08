package postservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import postservice.domain.Comment;
import postservice.domain.Post;
import postservice.feign.dto.PostCreateDateAndIdListDto;
import postservice.feign.dto.PostIdListDto;
import postservice.feign.dto.ServerUserDto;
import postservice.feign.UserServiceClientForPostService;
import postservice.kafka.PostEventDto;
import postservice.dto.response.ResponseCommentDetailDto;
import postservice.dto.response.ResponsePostDto;
import postservice.dto.request.RequestPostDto;
import postservice.exception.NotMatchWriterException;
import postservice.repository.CommentRepository;
import postservice.repository.LikesRepository;
import postservice.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClientForPostService userServiceClientForPostService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public PostService(PostRepository postRepository, LikesRepository likesRepository, CommentRepository commentRepository, KafkaTemplate<String, Object> kafkaTemplate,UserServiceClientForPostService userServiceClientForPostService) {
        this.postRepository = postRepository;
        this.likesRepository = likesRepository;
        this.commentRepository = commentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.userServiceClientForPostService = userServiceClientForPostService;
    }


    public void createpost(RequestPostDto request, Long userId) {
        Post post = request.toEntity(userId);
        postRepository.save(post);
        kafkaTemplate.send("post-event", new PostEventDto("CREATED", post.getCreateDateAndId(), post.getWriterId()));
    }
    public void updatepost(RequestPostDto request, Long postId,Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        if (post.getId() != null && post.getWriterId().equals(userId)){
            post.updatePost(request.getContent(),request.getTitle());
            postRepository.save(post);
        }
        else {
            throw new NotMatchWriterException();
        }
    }

    public List<ResponseCommentDetailDto> getCommentsWithUserDetails(Long postId, Long userId){
        List<Comment> comments = commentRepository.findByPostId(postId);

        // 댓글과 사용자 정보를 결합하여 CommentResponse 생성
        return comments.stream()
                .map(comment -> {
                    // Feign Client를 사용해 User 정보 가져오기
                    ServerUserDto user = userServiceClientForPostService.getUserById(comment.getWriterId());
                    // CommentResponse에 댓글 내용과 사용자 정보 추가
                    return new ResponseCommentDetailDto(
                            comment, user, userId
                    );
                })
                .collect(Collectors.toList());
    }

    public ResponsePostDto detailspost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        postRepository.save(post);
        Long likeCnt = likesRepository.countByPostId(postId);
        List<ResponseCommentDetailDto> commentList = getCommentsWithUserDetails(postId, userId);
        Boolean checkLike = likesRepository.existsByPostIdAndWriterId(postId, userId);
        // 내가 좋아요를 눌렀는지
        return new ResponsePostDto(post, userId, likeCnt, commentList, checkLike);
    }

    public void deletepost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        if (post.getId() != null && post.getWriterId().equals(userId)){
            kafkaTemplate.send("post-event", new PostEventDto("DELETED",post.getCreateDateAndId(), post.getWriterId()));
            postRepository.deleteById(postId);
        }
        else {
            throw new NotMatchWriterException();
        }
    }

    public List<ServerUserDto> getPostsById(Long userId, List<String> postIdList) {

        //feign client로 userId의 정보를 가져옴
        List<ServerUserDto> users = userServiceClientForPostService.getUserListDetailsByIdList(postIdList);
        //가져온 post id로 newsfeeddto를 채움

        return users;
//        postRepository.findById(postIdList.stream().count)
        //return으로 dto를 보냄
    }

    public PostCreateDateAndIdListDto getPostCreateDateAndIdListById(Long userId) {
        //해당 유저의 최근 7일 posts를 가져옴
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<String> postCreateDateAndIdList = postRepository.findByCreateDateAfterAndWriterId(sevenDaysAgo, userId)
                .stream()
                .map(post -> String.valueOf(post.getCreateDateAndId()))
                .collect(Collectors.toList());
        return new PostCreateDateAndIdListDto(postCreateDateAndIdList);
    }
//
//    public ServerPostDto getCreateDateAndIdById(Long userId) {
//        List<String> postList = postRepository.findCreateDateAndIdByWriterId(userId);
//
//        return new ServerPostDto(postList);
//    }


    // 카프카 테스트
//    @KafkaListener(topics = "user-service-to-post-service-data-topic", groupId = "user-service-group")
//    public void  listenAData(String dataJson) throws JsonProcessingException {
//        // JSON 데이터를 AData 객체로 변환
////        Map<String, Object> dataMap = new ObjectMapper().readValue(dataJson, new TypeReference<Map<String, Object>>() {});
//        processData(dataJson);
//    }
//    public void processData(String dataMap) {
//        A dto = new A("dataMap");
//        log.info("---------------------------------------------------------------------??");
//        log.info(String.valueOf(dto.getDtoId()));
//    }

}
