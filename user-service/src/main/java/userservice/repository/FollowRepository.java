package userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import userservice.domain.Follow;
import userservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowingId(Long userId);
    List<Follow> findByFollowerId(Long userId);
    long countByFollowerId(Long userId);
    long countByFollowingId(Long userId);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowingAndFollower(User followee, User user);
}