package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.domain.Comment;
import server.domain.Post;
import server.dto.request.RequestCommentDto;
import server.exception.NotMatchPostException;
import server.exception.NotMatchWriterException;
import server.repository.CommentRepository;
import server.repository.PostRepository;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    public void addcomment(RequestCommentDto request) {
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new RuntimeException("Post not found with id: " + request.getPostId()));
        // 프로젝트 id를 저장
        commentRepository.save(request.toEntity(post));
    }

    public void updatecomment(Long postId, Long userId, Long commentId, RequestCommentDto request) {
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
