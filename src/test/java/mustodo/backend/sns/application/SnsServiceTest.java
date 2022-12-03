package mustodo.backend.sns.application;

import mustodo.backend.common.support.DatabaseClearer;
import mustodo.backend.exception.sns.AlreadyFollowedException;
import mustodo.backend.exception.sns.NotFollowingUserException;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.sns.domain.Follow;
import mustodo.backend.sns.domain.FollowRepository;
import mustodo.backend.user.domain.Role;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.UserRepository;
import mustodo.backend.user.domain.embedded.EmailAuth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SnsServiceTest {

    @Autowired
    SnsService snsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    DatabaseClearer clearer;

    User user1;
    User user2;
    User user3;

    @BeforeEach
    void init() {
        user1 = new User(1L, "test2@test.test", "test2", "test", Role.USER, "", new ArrayList<>(), new ArrayList<>(), null, new EmailAuth("123456", true));
        user2 = new User(2L, "test1@test.test", "test", "test", Role.USER, "", new ArrayList<>(), new ArrayList<>(), null, new EmailAuth("123456", true));
        user3 = new User(3L, "test1@test.test", "test", "test", Role.USER, "", new ArrayList<>(), new ArrayList<>(), null, new EmailAuth("123456", true));
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

    @AfterEach
    void clear() {
        clearer.clear();
    }

    @Nested
    @DisplayName("follow 테스트")
    class FollowTest {

        @Test
        @DisplayName("팔로우 성공 테스트")
        void successTest() {
            //given
            Follow expect = new Follow(user1, user2);

            //when
            snsService.follow(user1, user2.getId());

            //then
            Follow follow = followRepository.findById(1L).get();
            assertThat(follow).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expect);
        }

        @Test
        @DisplayName("팔로우 실패 - 없는 유저")
        void failTest_userNotFound() {
            //given
            Long followerId = 0L;
            UserNotFoundException e = new UserNotFoundException(followerId);

            //when then
            assertThatThrownBy(() -> snsService.follow(user1, followerId))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("팔로우 실패 - 이미 팔로우 중인 유저")
        void failTest_alreadyFollowed() {
            //given
            Follow follow = new Follow(user1, user2);
            followRepository.save(follow);
            AlreadyFollowedException e = new AlreadyFollowedException();

            //when then
            assertThatThrownBy(() -> snsService.follow(user1, user2.getId()))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }

    @Nested
    @DisplayName("unfollow 테스트")
    class UnFollowTest {

        @Test
        @Transactional
        @DisplayName("언팔로우 성공 테스트")
        void successTest() {
            //given
            Follow save = followRepository.save(new Follow(user1, user2));

            //when
            snsService.unfollow(user1, user2.getId());

            //then
            boolean res = followRepository.findById(save.getId()).isPresent();
            assertThat(res).isFalse();
        }

        @Test
        @DisplayName("언팔로우 실패 - 팔로우를 안하고 있는 경우")
        void failTest_alreadyFollowed() {
            //given
            NotFollowingUserException e = new NotFollowingUserException();

            //when then
            assertThatThrownBy(() -> snsService.unfollow(user1, user2.getId()))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }
}