package postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import postservice.domain.Comment;
import postservice.domain.Post;
import postservice.dto.request.RequestCreateCommentDto;
import postservice.dto.request.RequestUpdateCommentDto;
import postservice.exception.NotMatchPostException;
import postservice.exception.NotMatchWriterException;
import postservice.repository.CommentRepository;
import postservice.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    public void addcomment(RequestCreateCommentDto request, Long postId, Long id) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        // 프로젝트 id를 저장
        commentRepository.save(request.toEntity(post, id));
    }

    public void updatecomment(Long postId, Long userId, Long commentId, RequestUpdateCommentDto request) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        if (comment.getWriterId() != null && comment.getWriterId().equals(userId)){
            if (comment.getPost()!=null && comment.getPost().getId().equals(postId)){
                comment.updateComment(request.getContent());
                commentRepository.save(comment);
            }
            else{
                throw new NotMatchPostException();
            }
        }
        else {
            throw new NotMatchWriterException();
        }
    }

    public void deletecomment(Long postId, Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        if (comment.getWriterId() != null && comment.getWriterId().equals(userId)){
            if (comment.getPost()!=null && comment.getPost().getId().equals(postId)){
                commentRepository.deleteById(commentId);
            }
            else {
                throw new NotMatchPostException();
            }
        }else {
            throw new NotMatchWriterException();
        }
    }
}
