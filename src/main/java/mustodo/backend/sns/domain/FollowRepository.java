package mustodo.backend.sns.domain;

import mustodo.backend.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowing(User following, User follower);
    @Query("select follow from Follow follow where follow.follower=:follower and follow.following.id=:followingId")
    Optional<Follow> findByFollowerAndFollowing_Id(User follower, Long followingId);

    @Query("select follow.following from Follow follow where follow.follower=:follower ")
    List<User> findFollowingsByFollower(User follower, Pageable pageable);
}
