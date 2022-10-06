package mustodo.backend.service.user.mail;

import mustodo.backend.config.EmailConfig;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.EmailAuthDto;
import mustodo.backend.entity.User;
import mustodo.backend.entity.embedded.EmailAuth;
import mustodo.backend.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static mustodo.backend.enums.error.SignUpErrorCode.INVALID_EMAIL_AUTH_KEY;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EmailAuthServiceUnitTest {

    @InjectMocks
    EmailAuthService emailAuthService;

    @Mock
    EmailConfig emailConfig;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    EmailKeyGenerator emailKeyGenerator;

    EmailAuthDto dto;

    User user;

    @Test
    @DisplayName("유저 인증 성공")
    void authUserSuccessTest() {
        //given
        user = User.builder()
                .emailAuth(new EmailAuth("123456", false))
                .build();
        dto = EmailAuthDto.builder()
                .authKey("123456")
                .build();

        //when
        MessageDto messageDto = emailAuthService.authUser(user, dto);

        //then
        assertThat(messageDto.getMessage()).isEqualTo(EMAIL_AUTH_SUCCESS);
    }

    @Test
    @DisplayName("유저 인증 실패")
    void authUserFailTest() {
        //given
        user = User.builder()
                .emailAuth(new EmailAuth("123456", false))
                .build();
        dto = EmailAuthDto.builder()
                .authKey("456789")
                .build();

        //when
        assertThatThrownBy(() -> emailAuthService.authUser(user, dto))
                .isInstanceOf(UserException.class)
                .hasMessage(INVALID_EMAIL_AUTH_KEY.getErrMsg());
    }

    @Test
    @DisplayName("인증 메일 전송 성공")
    void sendMessageSuccessTest() {
        //given
        user = User.builder()
                .email("email@email.com")
                .emailAuth(new EmailAuth(null, false))
                .build();
        String expectedEmailKey = "123456";
        MimeMessage message = new MimeMessage((Session) null);
        given(emailKeyGenerator.createKey())
                .willReturn(expectedEmailKey);
        given(javaMailSender.createMimeMessage())
                .willReturn(message);
        given(emailConfig.getAdmin())
                .willReturn("admin");
        given(emailConfig.getAdminId())
                .willReturn("admin@admin.com");

        //when
        String emailKey = emailAuthService.sendMail(user);

        //then
        assertThat(emailKey).isEqualTo(expectedEmailKey);
        assertThat(user.getEmailAuthKey()).isEqualTo(expectedEmailKey);
    }
}