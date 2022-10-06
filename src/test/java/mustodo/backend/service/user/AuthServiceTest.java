package mustodo.backend.service.user;

import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.SignUpRequestDto;
import mustodo.backend.exception.UserException;
import mustodo.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static mustodo.backend.enums.error.SignUpErrorCode.ALREADY_EXIST_EMAIL;
import static mustodo.backend.enums.error.SignUpErrorCode.PASSWORD_CONFIRM_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.UNCHECK_TERMS_AND_CONDITION;
import static mustodo.backend.enums.response.UserResponseMsg.SIGN_UP_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    SignUpRequestDto dto;

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccessTest() {
        //given
        dto = SignUpRequestDto.builder()
                .email("test")
                .name("test")
                .password("test")
                .passwordConfirm("test")
                .isTermsAndConditions(true)
                .build();
        given(userRepository.existsByEmail(dto.getEmail()))
                .willReturn(false);
        given(passwordEncoder.encode(dto.getPassword()))
                .willReturn("encodedPassword");

        //when
        MessageDto messageDto = authService.signUp(dto);

        //then
        assertThat(messageDto.getMessage()).isEqualTo(SIGN_UP_SUCCESS);
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
                .isTermsAndConditions(false)
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
        dto = SignUpRequestDto.builder()
                .email("test")
                .name("test")
                .password("test")
                .passwordConfirm("test")
                .isTermsAndConditions(true)
                .build();
        given(userRepository.existsByEmail(dto.getEmail()))
                .willReturn(true);

        //when then
        assertThatThrownBy(() -> authService.signUp(dto))
                .isInstanceOf(UserException.class)
                .hasMessage(ALREADY_EXIST_EMAIL.getErrMsg());
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
                .isTermsAndConditions(true)
                .build();
        given(userRepository.existsByEmail(dto.getEmail()))
                .willReturn(false);

        //when then
        assertThatThrownBy(() -> authService.signUp(dto))
                .isInstanceOf(UserException.class)
                .hasMessage(PASSWORD_CONFIRM_FAILED.getErrMsg());
    }
}