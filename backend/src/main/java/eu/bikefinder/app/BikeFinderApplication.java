package eu.bikefinder.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;

@SpringBootApplication(exclude = MailSenderAutoConfiguration.class)
public class BikeFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(BikeFinderApplication.class, args);
    }
}
