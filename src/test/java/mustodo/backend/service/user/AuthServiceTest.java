package mustodo.backend.service.user;

import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.auth.EmailAuthDto;
import mustodo.backend.dto.auth.LoginDto;
import mustodo.backend.dto.auth.SignUpRequestDto;
import mustodo.backend.entity.User;
import mustodo.backend.entity.embedded.EmailAuth;
import mustodo.backend.enums.error.LoginErrorCode;
import mustodo.backend.exception.UserException;
import mustodo.backend.repository.UserRepository;
import mustodo.backend.service.user.mail.EmailAuthSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXISTS_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXISTS_NAME;
import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_MESSAGE_CREATE_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.INVALID_EMAIL_AUTH_KEY;
import static mustodo.backend.enums.error.SignUpErrorCode.NOT_EXIST_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SEND_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SUCCESS;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    EmailAuthSender emailAuthSender;

    User user;

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        LoginDto dto;
        String email;
        String password;
        @BeforeEach
        void init() {
            email = "test@test.test";
            password = "test";
        }
        @Test
        @DisplayName("로그인 성공")
        void loginSuccessTest() {
            //given
            dto = LoginDto.builder()
                    .email(email)
                    .password(password)
                    .build();
            user = User.builder()
                    .id(1L)
                    .email(email)
                    .emailAuth(new EmailAuth("123123", true))
                    .password(password)
                    .name("test")
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(user));
            given(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                    .willReturn(true);

            //when
            User loginUser = authService.login(dto);

            assertThat(loginUser).isEqualTo(user);
        }

        @Test
        @DisplayName("로그인 실패 - 비밀번호 틀림")
        void loginFailTest_passwordNotCorrect() {
            //given
            dto = LoginDto.builder()
                    .email(email)
                    .password(password)
                    .build();
            user = User.builder()
                    .id(1L)
                    .email(email)
                    .emailAuth(new EmailAuth("123123", true))
                    .password(password)
                    .name("test")
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(user));
            given(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                    .willReturn(false);

            //when then
            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(LoginErrorCode.PASSWORD_NOT_CORRECT.getErrMsg());
        }

        @Test
        @DisplayName("로그인 실패 - 인증 안 된 유저")
        void loginFailTest_unAuthorizedUser() {
            //given
            dto = LoginDto.builder()
                    .email(email)
                    .password(password)
                    .build();
            user = User.builder()
                    .id(1L)
                    .email(email)
                    .emailAuth(new EmailAuth("123123", false))
                    .password(password)
                    .name("test")
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(user));
            given(passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                    .willReturn(true);
            given(emailAuthSender.sendAuthMail(user))
                    .willReturn("123456");

            //when then
            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(LoginErrorCode.NOT_AUTHORIZED_USER.getErrMsg());
            assertThat(user.getEmailAuthKey()).isEqualTo("123456");
        }

        @Test
        @DisplayName("로그인 실패 - 이메일 존재 x")
        void loginFailTest_notExistEmail() {
            //given
            dto = LoginDto.builder()
                    .email(email)
                    .password(password)
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.empty());

            //when then
            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(LoginErrorCode.NOT_EXIST_EMAIL.getErrMsg());
        }
    }

    @Nested
    @DisplayName("이메일 인증 테스트")
    class AuthorizeUserTest {

        EmailAuthDto dto;

        String email;

        @BeforeEach
        void init() {
            String email = "email@email.com";
            user = User.builder()
                    .email(email)
                    .emailAuth(new EmailAuth("123456", false))
                    .build();
        }

        @Test
        @DisplayName("유저 인증 성공")
        void authUserSuccessTest() {
            //given
            dto = EmailAuthDto.builder()
                    .authKey("123456")
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(user));

            //when
            MessageDto messageDto = authService.authorizeUser(dto);

            //then
            assertThat(messageDto.getMessage()).isEqualTo(EMAIL_AUTH_SUCCESS.getResMsg());
            assertThat(user.isAuthorizedUser()).isTrue();
        }

        @Test
        @DisplayName("유저 인증 실패: 잘못된 인증 번호")
        void authUserFailTest_wrongAuthKey() {
            //given
            dto = EmailAuthDto.builder()
                    .authKey("456789")
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(user));

            //when
            assertThatThrownBy(() -> authService.authorizeUser(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(INVALID_EMAIL_AUTH_KEY.getErrMsg());
        }

        @Test
        @DisplayName("유저 인증 실패: 없는 이메일")
        void authUserFailTest() {
            //given
            dto = EmailAuthDto.builder()
                    .email("wrongEmail@email.com")
                    .authKey("123456")
                    .build();
            given(userRepository.findByEmail("wrongEmail@email.com"))
                    .willReturn(Optional.empty());

            //when
            assertThatThrownBy(() -> authService.authorizeUser(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(NOT_EXIST_EMAIL.getErrMsg());
        }
    }

    @Nested
    @DisplayName("회원 가입 테스트")
    class SignUpTest {

        SignUpRequestDto dto;

        @BeforeEach
        void init() {
            dto = SignUpRequestDto.builder()
                    .email("test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("test")
                    .termsAndConditions(true)
                    .build();
        }

        @Test
        @DisplayName("회원가입 성공")
        void signUpSuccessTest() {
            //given
            user = User.builder()
                    .emailAuth(new EmailAuth(null, false))
                    .build();
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(userRepository.save(user))
                    .willReturn(user);
            given(passwordEncoder.encode(dto.getPassword()))
                    .willReturn("encodedPassword");
            given(emailAuthSender.sendAuthMail(any()))
                    .willReturn("123456");

            //when
            MessageDto messageDto = authService.signUp(dto);

            //then
            assertThat(messageDto.getMessage()).isEqualTo(SIGN_UP_SUCCESS.getResMsg());
        }

        @Test
        @DisplayName("회원가입 실패: 약관 동의 안함")
        void signUpFailedTest_uncheckTermsAndCondition() {
            //given
            dto = SignUpRequestDto.builder()
                    .email("test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("test")
                    .termsAndConditions(false)
                    .build();

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(UNCHECK_TERMS_AND_CONDITION.getErrMsg());
        }

        @Test
        @DisplayName("회원가입 실패: 이미 있는 이메일")
        void signUpFailedTest_alreadyExistEmail() {
            //given
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(true);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(ALREADY_EXISTS_EMAIL.getErrMsg());
        }

        @Test
        @DisplayName("회원가입 실패: 이미 있는 이름")
        void signUpFailedTest_alreadyExistName() {
            //given
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(userRepository.existsByName(dto.getName()))
                    .willReturn(true);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(ALREADY_EXISTS_NAME.getErrMsg());
        }

        @Test
        @DisplayName("회원가입 실패: 비밀번호 인증 실패")
        void signUpFailedTest_passwordConfirmFailed() {
            //given
            dto = SignUpRequestDto.builder()
                    .email("test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("asdfasdf")
                    .termsAndConditions(true)
                    .build();
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(PASSWORD_CONFIRM_FAILED.getErrMsg());
        }

        @Test
        @DisplayName("회원가입 실패: 인증 메일 전송 실패")
        void signUpFailedTest_authMailCreateFailed() {
            //given
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(emailAuthSender.sendAuthMail(any()))
                    .willThrow(new UserException(EMAIL_AUTH_SEND_FAILED, EMAIL_MESSAGE_CREATE_FAILED));

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(UserException.class)
                    .hasMessage(EMAIL_MESSAGE_CREATE_FAILED.getErrMsg());
        }
    }
}