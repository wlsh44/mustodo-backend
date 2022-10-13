package mustodo.backend.service.user.mail;

import mustodo.backend.config.EmailConfig;
import mustodo.backend.dto.user.EmailAuthDto;
import mustodo.backend.entity.User;
import mustodo.backend.entity.embedded.EmailAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EmailAuthSenderUnitTest {

    @InjectMocks
    EmailAuthSender emailAuthSender;

    @Mock
    EmailConfig emailConfig;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    EmailKeyGenerator emailKeyGenerator;

    EmailAuthDto dto;

    User user;



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
        String emailKey = emailAuthSender.sendAuthMail(user);

        //then
        assertThat(emailKey).isEqualTo(expectedEmailKey);
    }
}