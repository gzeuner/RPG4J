package de.zeus.hermes.config;

import de.zeus.hermes.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Configuration class for setting up the {@link JavaMailSender}.
 * Reads mail properties (host, port, username, password, etc.) from the environment,
 * decrypts them if encrypted, and configures the mail sender accordingly.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final Environment env;

    /**
     * Creates and configures a {@link JavaMailSender} bean for sending emails.
     *
     * @return A configured {@link JavaMailSender} instance, or throws an exception if configuration fails critically.
     */
    @Bean
    public JavaMailSender javaMailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Default encryption key if not specified in environment
        final String encryptionKey = env.getProperty("encryption.key", "defaultSecretKey123");

        try {
            // Set mail host with a sensible default
            mailSender.setHost(decryptIfNeeded(env.getProperty("mail.host", "smtp.example.com"), encryptionKey));

            // Set port with parsing and fallback to avoid conversion errors
            String portStr = env.getProperty("mail.port", "25"); // Default to 25 if not specified
            int port;
            try {
                port = Integer.parseInt(portStr.trim());
            } catch (NumberFormatException e) {
                log.warn("Invalid mail.port value '{}'. Falling back to default port 25.", portStr);
                port = 25; // Fallback to standard SMTP port
            }
            mailSender.setPort(port);

            // Set username and password, decrypting if necessary
            mailSender.setUsername(decryptIfNeeded(env.getProperty("mail.username", ""), encryptionKey));
            mailSender.setPassword(decryptIfNeeded(env.getProperty("mail.password", ""), encryptionKey));

            // Configure additional mail properties
            final Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", env.getProperty("mail.properties.mail.smtp.auth", "false"));
            props.put("mail.smtp.starttls.enable", env.getProperty("mail.properties.mail.smtp.starttls.enable", "false"));
            // Set the default sender address
            props.put("mail.from", decryptIfNeeded(env.getProperty("mail.from", "noreply@example.com"), encryptionKey));

            // Log successful configuration details
            log.info("Mail server configured: Host={}, Port={}, From={}",
                    mailSender.getHost(), mailSender.getPort(), props.get("mail.from"));
            return mailSender;

        } catch (Exception e) {
            // Log and rethrow critical configuration errors
            log.error("Error configuring mail server: {}", e.getMessage(), e);
            throw new RuntimeException("Mail server configuration failed", e);
        }
    }

    /**
     * Decrypts a value if it is in an encrypted format (ENC(...)).
     * If decryption fails or the value isn’t encrypted, returns the original value or null.
     *
     * @param value         The value to potentially decrypt.
     * @param encryptionKey The key used for decryption.
     * @return The decrypted value if encrypted, otherwise the original value; null if decryption fails.
     */
    private String decryptIfNeeded(final String value, final String encryptionKey) {
        if (value == null) {
            return null; // Handle null input gracefully
        }
        if (EncryptionUtil.isEncrypted(value)) {
            try {
                final String decryptedValue = EncryptionUtil.decrypt(encryptionKey, EncryptionUtil.extractEncrypted(value));
                return Objects.requireNonNull(decryptedValue, "Decryption failed");
            } catch (Exception e) {
                log.error("Failed to decrypt value '{}': {}", value, e.getMessage(), e);
                return null; // Return null on decryption failure instead of throwing
            }
        }
        return value; // Return original value if not encrypted
    }
}