package mustodo.backend.service.user.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.config.EmailConfig;
import mustodo.backend.dto.MessageDto;
import mustodo.backend.dto.user.EmailAuthDto;
import mustodo.backend.entity.User;
import mustodo.backend.exception.UserException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_MESSAGE_CREATE_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_SEND_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.INVALID_EMAIL_AUTH_KEY;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SEND_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_SUCCESS;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final EmailConfig emailConfig;
    private final JavaMailSender javaMailSender;
    private final EmailKeyGenerator keyGenerator;

    public MessageDto authUser(User user, EmailAuthDto dto) {
        String emailAuthKey = user.getEmailAuthKey();
        validateEmailKey(dto, emailAuthKey);
        user.authorizeUser();
        return MessageDto.builder()
                .message(EMAIL_AUTH_SUCCESS)
                .build();
    }

    private void validateEmailKey(EmailAuthDto dto, String emailAuthKey) {
        if (!dto.getAuthKey().equals(emailAuthKey)) {
            throw new UserException(EMAIL_AUTH_FAILED, INVALID_EMAIL_AUTH_KEY);
        }
    }

    public String sendMessage(User user) {
        String emailKey = keyGenerator.createKey();

        try {
            MimeMessage message = createMessage(user.getEmail(), emailKey);
            javaMailSender.send(message);
            user.setEmailAuthKey(emailKey);
        } catch (MailException e) {
            log.error("이메일 전송 실패, email: {}", user.getEmail());
            throw new UserException(EMAIL_AUTH_SEND_FAILED, EMAIL_SEND_FAILED);
        } catch (Exception e) {
            throw new UserException(EMAIL_AUTH_SEND_FAILED, EMAIL_MESSAGE_CREATE_FAILED);
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
