package mustodo.backend.auth.application;

import mustodo.backend.auth.application.AuthService;
import mustodo.backend.auth.ui.dto.EmailAuthDto;
import mustodo.backend.auth.ui.dto.LoginDto;
import mustodo.backend.auth.ui.dto.SignUpRequestDto;
import mustodo.backend.auth.domain.User;
import mustodo.backend.auth.domain.embedded.EmailAuth;
import mustodo.backend.exception.auth.EmailMessageCreateFailException;
import mustodo.backend.exception.auth.EmailSendFailException;
import mustodo.backend.exception.auth.IdPasswordNotCorrectException;
import mustodo.backend.exception.auth.InvalidEmailAuthKeyException;
import mustodo.backend.exception.auth.NotAuthorizedException;
import mustodo.backend.exception.auth.PasswordConfirmException;
import mustodo.backend.exception.auth.UncheckTermsAndConditionException;
import mustodo.backend.exception.user.EmailDuplicateException;
import mustodo.backend.exception.user.UserNameDuplicateException;
import mustodo.backend.exception.user.UserNotFoundException;
import mustodo.backend.user.UserRepository;
import mustodo.backend.auth.application.mail.EmailAuthSender;
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
            IdPasswordNotCorrectException e = new IdPasswordNotCorrectException();
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
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("로그인 실패 - 인증 안 된 유저")
        void loginFailTest_unAuthorizedUser() {
            //given
            NotAuthorizedException e = new NotAuthorizedException();
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
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
            assertThat(user.getEmailAuthKey()).isEqualTo("123456");
        }

        @Test
        @DisplayName("로그인 실패 - 이메일 존재 x")
        void loginFailTest_notExistEmail() {
            //given
            IdPasswordNotCorrectException e = new IdPasswordNotCorrectException();
            dto = LoginDto.builder()
                    .email(email)
                    .password(password)
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.empty());

            //when then
            assertThatThrownBy(() -> authService.login(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
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
            authService.authorizeUser(dto);

            //then
            assertThat(user.isAuthorizedUser()).isTrue();
        }

        @Test
        @DisplayName("유저 인증 실패: 잘못된 인증 번호")
        void authUserFailTest_wrongAuthKey() {
            //given
            String authKey = "456789";
            InvalidEmailAuthKeyException e = new InvalidEmailAuthKeyException(authKey);
            dto = EmailAuthDto.builder()
                    .authKey(authKey)
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(user));

            //when
            assertThatThrownBy(() -> authService.authorizeUser(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("유저 인증 실패: 없는 이메일")
        void authUserFailTest() {
            //given
            String email = "wrongEmail@email.com";
            UserNotFoundException e = new UserNotFoundException(email);
            dto = EmailAuthDto.builder()
                    .email(email)
                    .authKey("123456")
                    .build();
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.empty());

            //when
            assertThatThrownBy(() -> authService.authorizeUser(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
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
                    .id(1L)
                    .emailAuth(new EmailAuth(null, false))
                    .build();
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(userRepository.save(any()))
                    .willReturn(user);
            given(passwordEncoder.encode(dto.getPassword()))
                    .willReturn("encodedPassword");
            given(emailAuthSender.sendAuthMail(any()))
                    .willReturn("123456");

            //when
            Long userId = authService.signUp(dto);

            //then
            assertThat(userId).isEqualTo(1L);
        }

        @Test
        @DisplayName("회원가입 실패: 약관 동의 안함")
        void signUpFailedTest_uncheckTermsAndCondition() {
            //given
            UncheckTermsAndConditionException e = new UncheckTermsAndConditionException();
            dto = SignUpRequestDto.builder()
                    .email("test")
                    .name("test")
                    .password("test")
                    .passwordConfirm("test")
                    .termsAndConditions(false)
                    .build();

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패: 이미 있는 이메일")
        void signUpFailedTest_alreadyExistEmail() {
            EmailDuplicateException e = new EmailDuplicateException(dto.getEmail());
            //given
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(true);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패: 이미 있는 이름")
        void signUpFailedTest_alreadyExistName() {
            //given
            UserNameDuplicateException e = new UserNameDuplicateException(dto.getName());
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(userRepository.existsByName(dto.getName()))
                    .willReturn(true);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패: 비밀번호 인증 실패")
        void signUpFailedTest_passwordConfirmFailed() {
            //given
            PasswordConfirmException e = new PasswordConfirmException();
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
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패: 인증 메일 생성 실패")
        void signUpFailedTest_authMailCreateFailed() {
            //given
            EmailMessageCreateFailException e = new EmailMessageCreateFailException();
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(emailAuthSender.sendAuthMail(any()))
                    .willThrow(e);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }

        @Test
        @DisplayName("회원가입 실패: 인증 메일 전송 실패")
        void signUpFailedTest_authMailSendFail() {
            //given
            EmailSendFailException e = new EmailSendFailException();
            given(userRepository.existsByEmail(dto.getEmail()))
                    .willReturn(false);
            given(emailAuthSender.sendAuthMail(any()))
                    .willThrow(e);

            //when then
            assertThatThrownBy(() -> authService.signUp(dto))
                    .isInstanceOf(e.getClass())
                    .hasMessage(e.getMessage());
        }
    }
}