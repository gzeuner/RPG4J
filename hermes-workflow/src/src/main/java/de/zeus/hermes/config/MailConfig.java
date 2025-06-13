package de.zeus.hermes.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up the {@link JavaMailSender}.
 *
 * Credentials can be encrypted using Jasypt with ENC(...) syntax in application.yml.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final Environment env;

    @Bean
    public JavaMailSender javaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        try {
            // Host and port (host is optional; default used if missing)
            mailSender.setHost(env.getProperty("mail.host", "smtp.example.com"));

            int port = Integer.parseInt(env.getProperty("mail.port", "25").trim());
            mailSender.setPort(port);

            // Credentials (Jasypt will decrypt if needed)
            mailSender.setUsername(env.getProperty("mail.username", ""));
            mailSender.setPassword(env.getProperty("mail.password", ""));

            // Additional properties
            final Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", env.getProperty("mail.properties.mail.smtp.auth", "false"));
            props.put("mail.smtp.starttls.enable", env.getProperty("mail.properties.mail.smtp.starttls.enable", "false"));
            props.put("mail.from", env.getProperty("mail.from", "noreply@example.com"));

            log.info("📧 Mail server configured: Host={}, Port={}, From={}",
                    mailSender.getHost(), mailSender.getPort(), props.get("mail.from"));

            return mailSender;

        } catch (Exception e) {
            log.error("❌ Error configuring mail server: {}", e.getMessage(), e);
            throw new RuntimeException("Mail server configuration failed", e);
        }
    }
}
