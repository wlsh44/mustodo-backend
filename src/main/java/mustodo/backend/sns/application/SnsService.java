package mustodo.backend.sns.application;

import lombok.RequiredArgsConstructor;
import mustodo.backend.exception.sns.AlreadyFollowedException;
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
    public void follow(User user, Long followerId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException(followerId));
        validateFollow(user, follower);
        followRepository.save(new Follow(user, follower));
    }

    private void validateFollow(User user, User follower) {
        if (followRepository.existsByFollowingAndFollower(user, follower)) {
            throw new AlreadyFollowedException();
        }
    }
}
