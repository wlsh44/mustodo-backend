package mustodo.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "mail.smtp")
@PropertySource("classpath:mail.properties")
public class EmailConfig {

    private int port;
    private boolean auth;
    private boolean starttlsEnable;
    private String starttlsRequired;
    private String socketFactoryClass;
    private String socketFactoryFallback;
    private String socketFactoryPort;
    private String adminId;
    private String adminPassword;
    private String admin;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername(adminId);
        javaMailSender.setPassword(adminPassword);
        javaMailSender.setPort(port);
        javaMailSender.setJavaMailProperties(getJavaMailProperties());
        javaMailSender.setDefaultEncoding("UTF-8");
        return javaMailSender;
    }

    private Properties getJavaMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", auth);
        properties.put("mail.smtp.starttls.enable", starttlsEnable);
        properties.put("mail.smtp.starttls.required", starttlsRequired);
        properties.put("mail.smtp.socketFactory.port", socketFactoryPort);
        properties.put("mail.smtp.socketFactory.fallback", socketFactoryFallback);
        properties.put("mail.smtp.socketFactory.class", socketFactoryClass);
        return properties;
    }
}