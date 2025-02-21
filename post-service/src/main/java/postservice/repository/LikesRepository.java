package postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import postservice.domain.Likes;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    long countByPostId(Long postId);
    boolean existsByPostIdAndWriterId(Long postId, Long writerId);
    Optional<Likes> findByWriterIdAndPostId(Long writerId, Long postId);
}
