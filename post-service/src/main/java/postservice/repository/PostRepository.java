package postservice.repository;

import postservice.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import postservice.kafka.PostEventDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    List<String> findCreateDateAndIdByWriterId(Long userId);

    List<Post> findByCreateDateAfterAndWriterId(LocalDateTime sevenDaysAgo, Long userId);
}