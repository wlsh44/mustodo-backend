package mustodo.backend.sns.domain;

import mustodo.backend.sns.application.dto.TodoFeedQueryDto;
import mustodo.backend.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowingAndFollower(User following, User follower);
    @Query("select follow from Follow follow where follow.follower=:follower and follow.following.id=:followingId")
    Optional<Follow> findByFollowerAndFollowing_Id(User follower, Long followingId);

    @Query("select follow.following.id as userId, follow.following.name as userName, " +
            "todo.content as todoContent, todo.category.color as categoryColor " +
            "from Follow follow " +
            "left join Todo todo " +
            "on follow.following=todo.user " +
            "where follow.follower=:follower " +
            "and todo.date=:today " +
            "and todo.achieve=true " +
            "and todo.category.publicAccess=True")
    Page<TodoFeedQueryDto> findFollowsWithTodayAchievedTodo(User follower, LocalDate today, Pageable pageable);
}
