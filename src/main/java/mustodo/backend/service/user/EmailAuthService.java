package mustodo.backend.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mustodo.backend.config.EmailConfig;
import mustodo.backend.enums.response.UserResponseMsg;
import mustodo.backend.exception.UserException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_MESSAGE_CREATE_FAILED;
import static mustodo.backend.enums.error.SignUpErrorCode.EMAIL_SEND_FAILED;
import static mustodo.backend.enums.response.UserResponseMsg.EMAIL_AUTH_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    public static final int AUTH_KEY_LENGTH = 6;

    private final EmailConfig emailConfig;
    private final JavaMailSender javaMailSender;

    public String sendMessage(String to) {
         String contentMsg;

        try {
            MimeMessage message = createMessage(to);
            javaMailSender.send(message);
            contentMsg = (String) message.getContent();
        } catch (MailException e) {
            log.error("이메일 전송 실패, email: {}", to);
            throw new UserException(EMAIL_AUTH_FAILED, EMAIL_SEND_FAILED);
        } catch (Exception e) {
            throw new UserException(EMAIL_AUTH_FAILED, EMAIL_MESSAGE_CREATE_FAILED);
        }
        return contentMsg;
    }

    private MimeMessage createMessage(String to) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("Mustodo 이메일 인증");

        String emailKey = createKey();
        String authMessage = String.format("이메일 인증 번호: %s", emailKey);

        message.setText(authMessage, "UTF-8", "html");
        message.setFrom(new InternetAddress(emailConfig.getAdminId(), emailConfig.getAdmin()));
        return message;
    }

    private String createKey() {
        StringBuffer sb = new StringBuffer();

        Random random = new Random();
        for (int i = 0; i < AUTH_KEY_LENGTH; i++) {
            int num = random.nextInt(10);

            sb.append(num);
        }
        return sb.toString();
    }
}
