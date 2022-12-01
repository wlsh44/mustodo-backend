package mustodo.backend.sns.domain;

import mustodo.backend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowingAndFollower(User following, User follower);
}
