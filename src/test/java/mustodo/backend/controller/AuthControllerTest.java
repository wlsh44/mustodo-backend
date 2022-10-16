package mustodo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mustodo.backend.auth.ui.AuthController;
import mustodo.backend.dto.ErrorResponse;
import mustodo.backend.auth.ui.dto.EmailAuthDto;
import mustodo.backend.auth.ui.dto.LoginDto;
import mustodo.backend.auth.ui.dto.SignUpRequestDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.auth.application.AuthService;
import mustodo.backend.exception.auth.EmailMessageCreateFailException;
import mustodo.backend.exception.auth.EmailSendFailException;
import mustodo.backend.exception.auth.IdPasswordNotCorrectException;
import mustodo.backend.exception.auth.NotAuthorizedException;
import mustodo.backend.exception.auth.PasswordConfirmException;
import mustodo.backend.exception.auth.UncheckTermsAndConditionException;
import mustodo.backend.exception.user.EmailDuplicateException;
import mustodo.backend.exception.user.UserNameDuplicateException;
import mustodo.backend.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;


import static mustodo.backend.auth.ui.AuthController.LOGIN_SESSION_ID;
import static mustodo.backend.enums.error.BasicErrorCode.INVALID_ARGUMENT_ERROR;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;
    String uri;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {

        SignUpRequestDto dto;

        @BeforeEach
        void init() {
            uri = "/auth/sign-up";
            dto = SignUpRequestDto.builder()
                    .email("test@test.test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("test")
                    .termsAndConditions(true)
                    .build();
        }

        @Test
        @DisplayName("회원가입 성공")
        void successTest() throws Exception {
            //given

            //when then
            mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("회원가입 실패 - 이메일 형식 오류")
        void failTest_bindingException() throws Exception {
            //given
            dto = SignUpRequestDto.builder()
                    .email("test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("test")
                    .termsAndConditions(true)
                    .build();
            ErrorResponse expect = new ErrorResponse(INVALID_ARGUMENT_ERROR);

            //when then
            mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(expect)));
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
        void failTest_alreadyExistsEmail() throws Exception {
            //given
            EmailDuplicateException e = new EmailDuplicateException(dto.getEmail());
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.signUp(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이름")
        void failTest_alreadyExistsName() throws Exception {
            //given
            UserNameDuplicateException e = new UserNameDuplicateException(dto.getName());
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.signUp(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("회원가입 실패 - 약관 동의 안 함")
        void failTest_uncheckedTermsAndCondition() throws Exception {
            //given
            UncheckTermsAndConditionException e = new UncheckTermsAndConditionException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.signUp(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 확인 실패")
        void failTest_passwordNotCorrect() throws Exception {
            //given
            PasswordConfirmException e = new PasswordConfirmException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.signUp(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("유저 인증 테스트")
    class AuthorizeUserTest {

        EmailAuthDto dto;

        @BeforeEach
        void init() {
            uri = "/auth/authorization";
            dto = EmailAuthDto.builder()
                    .email("test@test.test")
                    .authKey("123456")
                    .build();
        }

        @Test
        @DisplayName("유저 인증 성공")
        void successTest() throws Exception {
            //given

            //when then
            mockMvc.perform(patch(uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("유저 인증 실패 - 잘못된 형식")
        void failTest_invalidArgument() throws Exception {
            //given
            dto = EmailAuthDto.builder()
                    .email("test@test.test")
                    .authKey("123")
                    .build();
            ErrorResponse errorResponse = new ErrorResponse(INVALID_ARGUMENT_ERROR);

            //when then
            mockMvc.perform(patch(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("유저 인증 실패 - 이메일 전송 실패")
        void failTest_sendMailFailed() throws Exception {
            //given
            EmailSendFailException e = new EmailSendFailException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(authService).authorizeUser(dto);

            //when then
            mockMvc.perform(patch(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }

        @Test
        @DisplayName("유저 인증 실패 - 이메일 생성 실패")
        void failTest_createMessageFailed() throws Exception {
            //given
            EmailMessageCreateFailException e = new EmailMessageCreateFailException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            doThrow(e).when(authService).authorizeUser(dto);


            //when then
            mockMvc.perform(patch(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)));
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        LoginDto dto;

        MockHttpSession session;

        User user;

        @BeforeEach
        void init() {
            uri = "/auth/login";
            dto = LoginDto.builder()
                    .email("test@test.test")
                    .password("test")
                    .build();
            user = User.builder()
                    .id(1L)
                    .email("test@test.test")
                    .password("test")
                    .name("name")
                    .build();
            session = new MockHttpSession();
        }

        @Test
        @DisplayName("로그인 성공")
        void loginSuccessTest() throws Exception {
            //given
            given(authService.login(dto))
                    .willReturn(user);

            //when then
            mockMvc.perform(post(uri)
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(""))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, user));
        }

        @Test
        @DisplayName("로그인 실패 - 이메일 존재 x")
        void loginFailTest_notExistEmail() throws Exception {
            //given
            UserNotFoundException e = new UserNotFoundException(dto.getEmail());
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.login(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())));
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 틀림")
        void loginFailTest_passwordNotCorrect() throws Exception {
            //given
            IdPasswordNotCorrectException e = new IdPasswordNotCorrectException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.login(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())));
        }

        @Test
        @DisplayName("로그인 실패 - 인증 안 된 유저")
        void loginFailTest_notAuthorizedUser() throws Exception {
            //given
            NotAuthorizedException e = new NotAuthorizedException();
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
            given(authService.login(dto))
                    .willThrow(e);

            //when then
            mockMvc.perform(post(uri)
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().json(objectMapper.writeValueAsString(errorResponse)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())));
        }
    }


    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {

        MockHttpSession session;

        @BeforeEach
        void init() {
            uri = "/auth/logout";
            session = new MockHttpSession();
        }

        @Test
        @DisplayName("로그아웃 성공 - 로그인 유저 있는 경우")
        void logoutSuccess_loginUserExists() throws Exception {
            //given
            User user = User.builder().id(1L).build();
            session.setAttribute(LOGIN_SESSION_ID, user);

            //when then
            mockMvc.perform(post(uri)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())))
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("로그아웃 성공 - 로그인 유저 없는 경우")
        void logoutSuccess_loginUserNotExist() throws Exception {
            //given

            //when then
            mockMvc.perform(post(uri)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())))
                    .andExpect(content().string(""));
        }
    }
}