package newsfeedservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import newsfeedservice.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
