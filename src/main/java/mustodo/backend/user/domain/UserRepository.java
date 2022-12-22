package mustodo.backend.user.domain;

import mustodo.backend.user.domain.User;
import mustodo.backend.user.ui.dto.MeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    Optional<User> findByEmail(String email);
    @Query("select user.followers.size as followerCount, user.followings.size as followingCount from User user where user=:user")
    Optional<MeResponse> findFollowCountByUser(User user);
}
