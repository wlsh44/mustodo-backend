package mustodo.backend.sns.application;

import lombok.RequiredArgsConstructor;
import mustodo.backend.exception.sns.AlreadyFollowedException;
import mustodo.backend.exception.sns.NotFollowingUserException;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.sns.domain.Follow;
import mustodo.backend.sns.domain.FollowRepository;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SnsService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(User user, Long followingId) {
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new UserNotFoundException(followingId));
        validateFollow(user, following);
        followRepository.save(new Follow(user, following));
    }

    private void validateFollow(User user, User following) {
        if (followRepository.existsByFollowingAndFollower(user, following)) {
            throw new AlreadyFollowedException();
        }
    }

    @Transactional
    public void unfollow(User user, Long followingId) {
        Follow follow = followRepository.findByFollowerAndFollowing_Id(user, followingId)
                .orElseThrow(NotFollowingUserException::new);
        followRepository.delete(follow);
    }
}
