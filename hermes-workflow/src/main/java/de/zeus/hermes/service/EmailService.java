package de.zeus.hermes.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Properties;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Service for sending emails with optional attachments.
 *
 * <p>
 * This service allows sending emails using a dynamically configured SMTP server.
 * It supports adding attachments and integrates seamlessly with other components
 * such as {@link WorkflowEngine} to enable automated email notifications.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender; // Injected mailSender (not used in dynamic configuration)

    /**
     * Sends an email using the specified SMTP server.
     *
     * @param smtpServer  the SMTP server to use for sending.
     * @param recipients  the list of email recipients.
     * @param subject     the subject of the email.
     * @param body        the email body (plain text or HTML).
     * @param attachments list of files to attach (optional).
     */
    public void sendEmail(final String smtpServer,
                          final List<String> recipients,
                          final String subject,
                          final String body,
                          final List<File> attachments) {
        try {
            // Configure a dynamic mail sender based on the provided SMTP server.
            final JavaMailSenderImpl dynamicMailSender = configureMailSender(smtpServer);
            // Create a MIME message.
            final MimeMessage message = dynamicMailSender.createMimeMessage();
            // Use MimeMessageHelper for easier message creation with multipart support.
            final MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set recipients, subject, and email body.
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(body, false);

            // Attach any provided files.
            attachFiles(helper, attachments);

            // Send the email.
            dynamicMailSender.send(message);
            log.info("Email successfully sent to: {}", recipients);
        } catch (MessagingException e) {
            log.error("Error creating or sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during email sending", e);
        }
    }

    /**
     * Configures a new {@link JavaMailSenderImpl} instance dynamically with the provided SMTP server.
     *
     * @param smtpServer the SMTP server hostname.
     * @return a configured {@link JavaMailSenderImpl} instance.
     */
    private JavaMailSenderImpl configureMailSender(final String smtpServer) {
        final JavaMailSenderImpl dynamicMailSender = new JavaMailSenderImpl();
        dynamicMailSender.setHost(smtpServer);
        // Set default SMTP port; adjust if needed.
        dynamicMailSender.setPort(25);
        // Set empty credentials assuming no authentication is required.
        dynamicMailSender.setUsername("");
        dynamicMailSender.setPassword("");

        // Set SMTP properties.
        final Properties props = dynamicMailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");
        return dynamicMailSender;
    }

    /**
     * Attaches files to an email if provided.
     *
     * @param helper      the {@link MimeMessageHelper} instance used to build the email.
     * @param attachments the list of files to attach.
     */
    private void attachFiles(final MimeMessageHelper helper, final List<File> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            log.info("No attachments provided.");
            return;
        }

        // Iterate through the list of files and add each attachment.
        for (final File file : attachments) {
            if (file.exists() && file.canRead()) {
                try {
                    helper.addAttachment(file.getName(), file);
                    log.info("Attachment added: {}", file.getName());
                } catch (MessagingException e) {
                    log.warn("Failed to add attachment: {}", file.getAbsolutePath(), e);
                }
            } else {
                log.warn("Attachment not found or unreadable: {}", file.getAbsolutePath());
            }
        }
    }
}
