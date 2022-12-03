package mustodo.backend.sns.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.exception.advice.dto.ErrorResponse;
import mustodo.backend.exception.sns.AlreadyFollowedException;
import mustodo.backend.exception.sns.NotFollowingUserException;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.sns.application.SnsService;
import mustodo.backend.user.domain.User;
import mustodo.backend.user.domain.embedded.EmailAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = SnsController.class)
class SnsControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    SnsService snsService;

    User user;

    MockHttpSession session;

    String uri;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
        user = User.builder()
                .emailAuth(new EmailAuth("123123", true))
                .build();
        session = new MockHttpSession();
        session.setAttribute(LOGIN_SESSION_ID, user);
    }

    @Nested
    @DisplayName("팔로우 테스트")
    class FollowTest {

        @BeforeEach
        void init() {
            uri = "/api/sns/follow/{followingId}";
        }

        @Test
        @DisplayName("성공 테스트")
        void successTest() throws Exception {
            //given
            Long followingId = 2L;

            //when then
            mockMvc.perform(post(uri, followingId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("실패 테스트 - 없는 유저")
        void failTest_userNotFound() throws Exception {
            //given
            Long followingId = 2L;
            UserNotFoundException e = new UserNotFoundException(followingId);
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(snsService).follow(user, followingId);

            //when then
            mockMvc.perform(post(uri, followingId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("실패 테스트 - 이미 팔로우 한 유저")
        void failTest_alreadyFollowed() throws Exception {
            //given
            Long followingId = 2L;
            AlreadyFollowedException e = new AlreadyFollowedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(snsService).follow(user, followingId);

            //when then
            mockMvc.perform(post(uri, followingId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("언팔로우 테스트")
    class UnfollowTest {

        @BeforeEach
        void init() {
            uri = "/api/sns/unfollow/{followingId}";
        }

        @Test
        @DisplayName("성공 테스트")
        void successTest() throws Exception {
            //given
            Long followingId = 2L;

            //when then
            mockMvc.perform(post(uri, followingId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("실패 테스트 - 팔로우 하지 않은 유저")
        void failTest_alreadyFollowed() throws Exception {
            //given
            Long followingId = 2L;
            NotFollowingUserException e = new NotFollowingUserException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(snsService).unfollow(user, followingId);

            //when then
            mockMvc.perform(post(uri, followingId)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mapper.writeValueAsString(errorResponse)));
        }
    }
}