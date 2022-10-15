package mustodo.backend.service.auth.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.config.EmailConfig;
import mustodo.backend.entity.User;
import mustodo.backend.exception.AuthException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_MESSAGE_CREATE_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_SEND_FAILED;
import static mustodo.backend.enums.response.AuthResponseMsg.EMAIL_AUTH_SEND_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAuthSender {

    private final EmailConfig emailConfig;
    private final JavaMailSender javaMailSender;
    private final EmailKeyGenerator keyGenerator;


    public String sendAuthMail(User user) {
        String emailKey = keyGenerator.createKey();

        try {
            MimeMessage message = createMessage(user.getEmail(), emailKey);
            javaMailSender.send(message);
            log.info("이메일 전송, email: {} 인증 번호: {}", user.getEmail(), emailKey);
        } catch (MailException e) {
            log.error(e.getMessage());
            log.error("이메일 전송 실패, email: {}", user.getEmail());
            throw new AuthException(EMAIL_AUTH_SEND_FAILED, EMAIL_SEND_FAILED);
        } catch (Exception e) {
            throw new AuthException(EMAIL_AUTH_SEND_FAILED, EMAIL_MESSAGE_CREATE_FAILED);
        }
        return emailKey;
    }

    private MimeMessage createMessage(String to, String emailKey) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("Mustodo 이메일 인증");

        String authMessage = String.format("이메일 인증 번호: %s", emailKey);

        message.setText(authMessage, "UTF-8", "html");
        message.setFrom(new InternetAddress(emailConfig.getAdminId(), emailConfig.getAdmin()));
        return message;
    }
}
