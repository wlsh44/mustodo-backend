package mustodo.backend.sns.application;

import lombok.RequiredArgsConstructor;
import mustodo.backend.exception.sns.AlreadyFollowedException;
import mustodo.backend.exception.sns.NotFollowingUserException;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.sns.application.dto.FeedTodoQueryDto;
import mustodo.backend.sns.domain.Follow;
import mustodo.backend.sns.domain.FollowRepository;
import mustodo.backend.sns.ui.dto.FeedTodoMapDto;
import mustodo.backend.sns.ui.dto.FeedTodoResponse;
import mustodo.backend.todo.domain.TodoRepository;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.UserRepository;
import mustodo.backend.user.domain.embedded.Image;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnsService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public void follow(User user, Long followingId) {
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException(followingId));
        validateFollow(user, following);
        followRepository.save(new Follow(user, following));
    }

    private void validateFollow(User user, User following) {
        if (followRepository.existsByFollowerAndFollowing(user, following)) {
            throw new AlreadyFollowedException();
        }
    }

    @Transactional
    public void unfollow(User user, Long followingId) {
        Follow follow = followRepository.findByFollowerAndFollowing_Id(user, followingId)
                .orElseThrow(NotFollowingUserException::new);
        followRepository.delete(follow);
    }

    public List<FeedTodoResponse> findTodoFeed(User user, Pageable pageable, HttpServletRequest request) {
        List<FeedTodoQueryDto> todayAchievedTodo = todoRepository.findByTodayAchieveTrue(LocalDate.now());
        FeedTodoMapDto feedTodoMapDto = FeedTodoMapDto.from(todayAchievedTodo, Image.getBaseUrl(request));
        return feedTodoMapDto.toResponse();
    }
}
