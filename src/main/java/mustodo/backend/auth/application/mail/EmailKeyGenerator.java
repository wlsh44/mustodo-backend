package mustodo.backend.auth.application.mail;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class EmailKeyGenerator {

    private static final int AUTH_KEY_LENGTH = 6;

    public String createKey() {
        StringBuilder sb = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < AUTH_KEY_LENGTH; i++) {
            int num = random.nextInt(10);

            sb.append(num);
        }
        return sb.toString();
    }
}
