package eu.bikefinder.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "ebf.mail.enabled", havingValue = "true")
public class MailSenderConfiguration {

    @Bean
    public JavaMailSender javaMailSender(Environment env) {
        String host = env.getProperty("spring.mail.host");
        if (host == null || host.isBlank()) {
            throw new IllegalStateException("ebf.mail.enabled=true requires spring.mail.host (set MAIL_HOST or spring.mail.host)");
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host.trim());
        sender.setPort(env.getProperty("spring.mail.port", Integer.class, 587));
        String user = env.getProperty("spring.mail.username", "");
        String password = env.getProperty("spring.mail.password", "");
        if (!user.isBlank()) {
            sender.setUsername(user);
            sender.setPassword(password);
        }
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        boolean auth = !user.isBlank();
        props.put("mail.smtp.auth", String.valueOf(auth));
        String startTls =
                env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", auth ? "true" : "false");
        props.put("mail.smtp.starttls.enable", startTls);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        return sender;
    }
}
