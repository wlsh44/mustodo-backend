package mustodo.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import mustodo.backend.dto.ErrorDto;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.EmailAuthDto;
import mustodo.backend.dto.user.LoginDto;
import mustodo.backend.dto.user.SignUpRequestDto;
import mustodo.backend.entity.User;
import mustodo.backend.enums.error.LoginErrorCode;
import mustodo.backend.exception.UserException;
import mustodo.backend.service.user.AuthService;
import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.io.UnsupportedEncodingException;

import static mustodo.backend.controller.UserController.LOGIN_SESSION_ID;
import static mustodo.backend.enums.error.LoginErrorCode.LOGIN_FAILED_ERROR;
import static mustodo.backend.enums.error.LoginErrorCode.NOT_AUTHORIZED_USER;
import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXISTS_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXISTS_NAME;
import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_MESSAGE_CREATE_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_SEND_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;
import static mustodo.backend.enums.response.BasicResponseMsg.INVALID_ARGUMENT_ERROR;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SUCCESS;
import static mustodo.backend.enums.response.UserResponseMsg.LOGIN_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.LOGIN_SUCCESS;
import static mustodo.backend.enums.response.UserResponseMsg.LOGOUT_SUCCESS;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

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
            MessageDto expect = MessageDto.builder()
                    .message(SIGN_UP_SUCCESS)
                    .build();
            given(authService.signUp(dto))
                    .willReturn(expect);

            //when
            MvcResult mvcResult = mockMvc.perform(post("/user")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsBytes(dto)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();

            //then
            MessageDto messageDto = getMessageDto(mvcResult);
            assertThat(messageDto).isEqualTo(expect);
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
            ErrorDto expect = ErrorDto.builder()
                    .message(INVALID_ARGUMENT_ERROR)
                    .build();

            //when
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            //then
            ErrorDto messageDto = getErrorDto(mvcResult);
            assertThat(messageDto).isEqualTo(expect);
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
        void failTest_alreadyExistsEmail() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, ALREADY_EXISTS_EMAIL));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(ALREADY_EXISTS_EMAIL.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 존재하는 이름")
        void failTest_alreadyExistsName() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, ALREADY_EXISTS_NAME));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(ALREADY_EXISTS_NAME.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패 - 약관 동의 안 함")
        void failTest_uncheckedTermsAndCondition() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, UNCHECK_TERMS_AND_CONDITION));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(UNCHECK_TERMS_AND_CONDITION.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패 - 비밀번호 확인 실패")
        void failTest_passwordNotCorrect() throws Exception {
            //given
            given(authService.signUp(dto))
                    .willThrow(new UserException(SIGN_UP_FAILED, PASSWORD_CONFIRM_FAILED));

            //when then
            MvcResult mvcResult = mockMvc.perform(post("/user")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(PASSWORD_CONFIRM_FAILED.name());
            assertThat(message).isEqualTo(SIGN_UP_FAILED.getResMsg());
        }
    }

    @Nested
    @DisplayName("유저 인증 테스트")
    class AuthorizeUserTest {

        EmailAuthDto dto;

        @BeforeEach
        void init() {
            dto = EmailAuthDto.builder()
                    .email("test@test.test")
                    .authKey("123456")
                    .build();
        }

        @Test
        @DisplayName("유저 인증 성공")
        void successTest() throws Exception {
            //given
            MessageDto expect = MessageDto.builder()
                    .message(EMAIL_AUTH_SUCCESS)
                    .build();
            given(authService.authorizeUser(dto))
                    .willReturn(expect);

            //when then
            MvcResult mvcResult = mockMvc.perform(put("/user/auth")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsBytes(dto)))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();

            MessageDto messageDto = getMessageDto(mvcResult);
            assertThat(messageDto).isEqualTo(expect);
        }

        @Test
        @DisplayName("유저 인증 실패 - 잘못된 형식")
        void failTest_invalidArgument() throws Exception {
            //given
            dto = EmailAuthDto.builder()
                    .email("test@test.test")
                    .authKey("123")
                    .build();
            ErrorDto expect = ErrorDto.builder()
                    .message(INVALID_ARGUMENT_ERROR)
                    .build();

            //when then
            MvcResult mvcResult = mockMvc.perform(put("/user/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            ErrorDto messageDto = getErrorDto(mvcResult);
            assertThat(messageDto).isEqualTo(expect);
        }

        @Test
        @DisplayName("유저 인증 실패 - 이메일 전송 실패")
        void failTest_sendMailFailed() throws Exception {
            //given
            given(authService.authorizeUser(dto))
                    .willThrow(new UserException(EMAIL_AUTH_FAILED, EMAIL_SEND_FAILED));

            //when then
            MvcResult mvcResult = mockMvc.perform(put("/user/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(EMAIL_SEND_FAILED.name());
            assertThat(message).isEqualTo(EMAIL_AUTH_FAILED.getResMsg());
        }

        @Test
        @DisplayName("유저 인증 실패 - 이메일 생성 실패")
        void failTest_createMessageFailed() throws Exception {
            //given
            given(authService.authorizeUser(dto))
                    .willThrow(new UserException(EMAIL_AUTH_FAILED, EMAIL_MESSAGE_CREATE_FAILED));

            //when then
            MvcResult mvcResult = mockMvc.perform(put("/user/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(dto)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            DocumentContext parse = getParsedContent(mvcResult);
            String errorCode = parse.read("$.errorCode");
            String message = parse.read("$.message");

            assertThat(errorCode).isEqualTo(EMAIL_MESSAGE_CREATE_FAILED.name());
            assertThat(message).isEqualTo(EMAIL_AUTH_FAILED.getResMsg());
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
            dto = LoginDto.builder()
                    .email("test@test.test")
                    .password("test")
                    .build();
            MessageDto message = MessageDto.builder()
                    .message(LOGIN_SUCCESS)
                    .build();
            given(authService.login(dto))
                    .willReturn(user);

            //when then
            mockMvc.perform(post("/user/login")
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(message)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, user));
        }

        @Test
        @DisplayName("로그인 실패 - 이메일 존재 x")
        void loginFailTest_notExistEmail() throws Exception {
            //given
            dto = LoginDto.builder()
                    .email("test@test.test")
                    .password("test")
                    .build();
            ErrorDto message = ErrorDto.builder()
                    .message(LOGIN_FAILED)
                    .errorCode(LOGIN_FAILED_ERROR)
                    .build();
            given(authService.login(dto))
                    .willThrow(new UserException(LOGIN_FAILED, LoginErrorCode.NOT_EXIST_EMAIL));

            //when then
            mockMvc.perform(post("/user/login")
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(message)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())));
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 틀림")
        void loginFailTest_passwordNotCorrect() throws Exception {
            //given
            dto = LoginDto.builder()
                    .email("test@test.test")
                    .password("test")
                    .build();
            ErrorDto message = ErrorDto.builder()
                    .message(LOGIN_FAILED)
                    .errorCode(LOGIN_FAILED_ERROR)
                    .build();
            given(authService.login(dto))
                    .willThrow(new UserException(LOGIN_FAILED, LoginErrorCode.PASSWORD_NOT_CORRECT));

            //when then
            mockMvc.perform(post("/user/login")
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(message)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())));
        }

        @Test
        @DisplayName("로그인 실패 - 인증 안 된 유저")
        void loginFailTest_notAuthorizedUser() throws Exception {
            //given
            dto = LoginDto.builder()
                    .email("test@test.test")
                    .password("test")
                    .build();
            ErrorDto message = ErrorDto.builder()
                    .message(LOGIN_FAILED)
                    .errorCode(NOT_AUTHORIZED_USER)
                    .build();
            given(authService.login(dto))
                    .willThrow(new UserException(LOGIN_FAILED, LoginErrorCode.NOT_AUTHORIZED_USER));

            //when then
            mockMvc.perform(post("/user/login")
                            .content(objectMapper.writeValueAsString(dto))
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(message)))
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())));
        }
    }


    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {

        MockHttpSession session;

        @BeforeEach
        void init() {
            session = new MockHttpSession();
        }

        @Test
        @DisplayName("로그아웃 성공 - 로그인 유저 있는 경우")
        void logoutSuccess_loginUserExists() throws Exception {
            //given
            MessageDto message = MessageDto.builder()
                    .message(LOGOUT_SUCCESS)
                    .build();
            User user = User.builder().id(1L).build();
            session.setAttribute(LOGIN_SESSION_ID, user);

            mockMvc.perform(post("/user/logout")
                            .session(session))
                    .andDo(print())
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())))
                    .andExpect(content().json(objectMapper.writeValueAsString(message)));
        }

        @Test
        @DisplayName("로그아웃 성공 - 로그인 유저 없는 경우")
        void logoutSuccess_loginUserNotExist() throws Exception {
            //given
            MessageDto message = MessageDto.builder()
                    .message(LOGOUT_SUCCESS)
                    .build();

            mockMvc.perform(post("/user/logout")
                            .session(session))
                    .andDo(print())
                    .andExpect(request().sessionAttribute(LOGIN_SESSION_ID, is(nullValue())))
                    .andExpect(content().json(objectMapper.writeValueAsString(message)));
        }
    }
    private DocumentContext getParsedContent(MvcResult mvcResult) throws UnsupportedEncodingException {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        return JsonPath.parse(contentAsString);
    }

    private MessageDto getMessageDto(MvcResult mvcResult) throws java.io.IOException {
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        return objectMapper.readValue(contentAsByteArray, MessageDto.class);
    }

    private ErrorDto getErrorDto(MvcResult mvcResult) throws java.io.IOException {
        byte[] contentAsByteArray = mvcResult.getResponse().getContentAsByteArray();
        return objectMapper.readValue(contentAsByteArray, ErrorDto.class);
    }
}