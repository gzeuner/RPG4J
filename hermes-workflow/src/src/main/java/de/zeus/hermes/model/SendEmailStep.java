package de.zeus.hermes.model;

import de.zeus.hermes.service.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * A {@link WorkflowStep} implementation that sends an email using {@link EmailService}.
 *
 * <p>
 * This step retrieves SMTP settings, recipient addresses, and attachments from the execution context
 * or configuration properties. It then composes and sends an email with the specified subject and body,
 * optionally including attachments. Designed for integration within automated workflows.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class SendEmailStep implements WorkflowStep {

    private static final String FALLBACK_PROPERTY = "mail.host";
    private static final String DEFAULT_EXPORT_DIR = "export"; // Fallback subdirectory for export path

    /**
     * The name of this email step, used for identification and logging.
     */
    private String name;

    /**
     * The SMTP server configuration, which can be a direct value, context key (e.g., "context:key"), or placeholder.
     */
    private String smtp;

    /**
     * The service responsible for sending emails.
     */
    private final EmailService emailService;

    /**
     * The Spring environment for resolving configuration properties.
     */
    private final Environment environment;

    /**
     * The export path for attachments, defaults to empty if not set in application.yml.
     */
    @Value("${export.file-path:}")
    private String exportPath;

    /**
     * Executes the email sending step by resolving SMTP, recipients, and attachments from the context.
     * <p>
     * If any step fails, an error message is stored in the context under "email_error", following a "log and continue" pattern.
     * </p>
     *
     * @param context the workflow context containing email data (e.g., subject, body, recipients); must not be null.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        log.info("Starting SendEmailStep - Name: '{}', SMTP: '{}', ExportPath: '{}'", getName(), smtp, exportPath);
        log.debug("Context before processing: {}", context);
        log.debug("Current working directory: {}", System.getProperty("user.dir"));

        if (context == null) {
            log.error("Context is null for step '{}'. Email sending aborted.", getName());
            return;
        }

        // Resolve SMTP server configuration
        final String smtpServer = resolveSmtpServer(context);
        final int smtpPort = resolveSmtpPort(context);

        if (smtpServer == null) {
            log.error("No valid SMTP server configured for step '{}'. Email sending aborted.", getName());
            context.put("email_error", "SMTP server missing");
            return;
        }

        // Resolve email recipients
        final List<String> recipients = resolveRecipients(context);
        if (recipients == null || recipients.isEmpty()) {
            log.error("Recipients missing or empty for step '{}'. Email sending aborted.", getName());
            context.put("email_error", "Recipients missing");
            return;
        }

        // Resolve email subject and body with fallback values
        final String subject = Optional.ofNullable((String) context.get("emailSubject"))
                .orElse("No subject specified");
        final String body = Optional.ofNullable((String) context.get("emailBody"))
                .orElse("No email body specified");

        // Resolve the export path for attachments
        final String resolvedExportPath = resolveExportPath();
        log.debug("Resolved export path: {}", resolvedExportPath);

        // Resolve attachments from transformedContent in the context
        final List<File> attachments = Optional.ofNullable((String) context.get("transformedContent"))
                .map(fileName -> Paths.get(resolvedExportPath, fileName).toFile())
                .filter(file -> {
                    boolean exists = file.exists();
                    if (!exists) {
                        log.warn("Attachment file not found for step '{}': {}", getName(), file.getAbsolutePath());
                    }
                    return exists;
                })
                .map(List::of)
                .orElseGet(() -> {
                    log.debug("No valid attachment found in 'transformedContent' for step '{}': {}", getName(), context.get("transformedContent"));
                    return List.of();
                });

        log.info("Sending email via SMTP '{}:{}' to recipients: {} with {} attachment(s): {}", smtpServer, smtpPort, recipients, attachments.size(), attachments);

        try {
            emailService.sendEmail(smtpServer, smtpPort, recipients, subject, body, attachments);
            log.info("Email successfully sent to: {} for step '{}'", recipients, getName());
        } catch (Exception e) {
            log.error("Error sending email for step '{}': {}", getName(), e.getMessage(), e);
            context.put("email_error", "Email sending failed: " + e.getMessage());
        }
    }

    /**
     * Resolves the export path for attachments, falling back to the working directory with an "export" subdirectory.
     *
     * @return the resolved export path as a string.
     */
    private String resolveExportPath() {
        if (exportPath == null || exportPath.trim().isEmpty()) {
            return Paths.get(System.getProperty("user.dir"), DEFAULT_EXPORT_DIR).toString();
        }
        if (exportPath.startsWith("classpath:")) {
            log.warn("Classpath is read-only for step '{}'. Using working directory with 'export' folder.", getName());
            return Paths.get(System.getProperty("user.dir"), DEFAULT_EXPORT_DIR).toString();
        }
        return Paths.get(exportPath).toString();
    }

    /**
     * Resolves the SMTP server from the smtp field, context, or fallback configuration.
     *
     * @param context the workflow context to check for SMTP values.
     * @return the resolved SMTP server, or null if none is found.
     */
    @SuppressWarnings("unchecked")
    private String resolveSmtpServer(final Map<String, Object> context) {
        if (smtp != null && !smtp.trim().isEmpty()) {
            if (smtp.startsWith("context:")) {
                final String contextKey = smtp.substring("context:".length());
                String directContextValue = Optional.ofNullable(context.get(contextKey))
                        .map(Object::toString)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .orElse(null);

                if (directContextValue != null) {
                    return directContextValue;
                }

                List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) context.get("sqlResult_" + contextKey);
                if (sqlResult != null && !sqlResult.isEmpty()) {
                    Object val = sqlResult.get(0).get("KEWALP");
                    if (val != null && !val.toString().isBlank()) {
                        return val.toString().trim();
                    }
                }

                if (smtp.contains("${")) {
                    String resolved = environment.resolvePlaceholders(smtp);
                    if (resolved != null && !resolved.equals(smtp)) {
                        return resolved.trim();
                    }
                }
                return getFallbackSmtpServer();
            }

            String resolved = environment.resolvePlaceholders(smtp);
            if (resolved != null && !resolved.equals(smtp)) {
                return resolved.trim();
            }
            return smtp.trim();
        }
        return getFallbackSmtpServer();
    }

    private int resolveSmtpPort(final Map<String, Object> context) {
        Object portObj = context.get("email.smtpPort");
        if (portObj instanceof Number) {
            return ((Number) portObj).intValue();
        }
        if (portObj instanceof String) {
            try {
                return Integer.parseInt(((String) portObj).trim());
            } catch (NumberFormatException ignored) {
                log.warn("Invalid SMTP port in context: '{}'", portObj);
            }
        }
        String configPort = environment.getProperty("mail.port");
        if (configPort != null && !configPort.isBlank()) {
            try {
                return Integer.parseInt(configPort.trim());
            } catch (NumberFormatException ignored) {
                log.warn("Invalid configured SMTP port: '{}'", configPort);
            }
        }
        log.warn("No valid SMTP port found. Falling back to default port 25.");
        return 25;
    }

    /**
     * Resolves the list of email recipients from the context.
     *
     * @param context the workflow context to check for recipient data.
     * @return a list of recipient email addresses, or null if none are found.
     */
    @SuppressWarnings("unchecked")
    private List<String> resolveRecipients(final Map<String, Object> context) {
        List<String> recipients = Optional.ofNullable((List<String>) context.get("email.recipients"))
                .filter(list -> !list.isEmpty())
                .orElseGet(() -> Optional.ofNullable((List<Map<String, Object>>) context.get("sqlResult_fetchRecipients"))
                        .filter(list -> !list.isEmpty())
                        .map(list -> list.stream()
                                .map(map -> map.get("WERT"))
                                .filter(wert -> wert != null && !wert.toString().trim().isEmpty())
                                .map(Object::toString)
                                .collect(Collectors.toList()))
                        .filter(list -> !list.isEmpty())
                        .orElse(null));

        if (recipients == null || recipients.isEmpty()) {
            String fallbackRecipient = environment.getProperty("mail.default-recipient");
            if (fallbackRecipient != null && !fallbackRecipient.isBlank()) {
                log.warn("No recipients found in context. Using fallback recipient from config: {}", fallbackRecipient);
                return List.of(fallbackRecipient.trim());
            } else {
                log.error("No recipients available and no fallback recipient configured in 'mail.default-recipient'.");
                return null;
            }
        }
        return recipients;
    }

    /**
     * Retrieves the fallback SMTP server from the environment properties.
     *
     * @return the fallback SMTP server, or null if not configured.
     */
    private String getFallbackSmtpServer() {
        return Optional.ofNullable(environment)
                .map(env -> env.getProperty(FALLBACK_PROPERTY))
                .filter(s -> !s.trim().isEmpty())
                .orElseGet(() -> {
                    log.warn("No fallback SMTP server found in application.properties for '{}' in step '{}'", FALLBACK_PROPERTY, getName());
                    return null;
                });
    }

    /**
     * Retrieves the name of this email step.
     * Returns a default name ("UnnamedStep") if the configured name is null or blank.
     *
     * @return the step's name, or "UnnamedStep" if not set.
     */
    @Override
    public String getName() {
        return Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .orElse("UnnamedStep");
    }
}
