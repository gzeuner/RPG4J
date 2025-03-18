package de.zeus.hermes.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Service for rendering templates using the FreeMarker template engine.
 * <p>
 * This service processes FreeMarker templates with provided data models and returns the rendered output as a string.
 * Designed to integrate with {@link WorkflowEngine}, it handles errors gracefully by logging them and returning a fallback value.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final Configuration freemarkerConfig;

    /**
     * Renders a FreeMarker template using the specified data model.
     * <p>
     * The template is loaded from the configured FreeMarker {@link Configuration}, processed with the given model,
     * and returned as a string. If the template name is invalid, the model is null, or rendering fails,
     * a fallback empty string is returned.
     * </p>
     *
     * @param templateName the name of the template file (e.g., "email.ftl"). Can include a "classpath:" prefix, which will be stripped.
     *                     Must not be null or blank.
     * @param model        the data model for the template, containing key-value pairs for substitution. Must not be null.
     * @return the rendered template content as a string, or an empty string if an error occurs.
     */
    public String renderTemplate(final String templateName, final Map<String, Object> model) {
        // Validate template name
        if (templateName == null || templateName.isBlank()) {
            log.warn("Template name is empty or null.");
            return "";
        }
        // Validate data model
        if (model == null) {
            log.warn("Data model is null for template '{}'.", templateName);
            return "";
        }

        // Normalize the template name by removing a possible "classpath:" prefix.
        final String normalizedTemplateName = templateName.replace("classpath:", "");

        try {
            // Load the FreeMarker template from the configuration.
            final Template template = freemarkerConfig.getTemplate(normalizedTemplateName);
            // Use try-with-resources to manage the StringWriter.
            try (final StringWriter writer = new StringWriter()) {
                // Process the template with the provided model.
                template.process(model, writer);
                log.info("Template '{}' rendered successfully.", normalizedTemplateName);
                return writer.toString();
            }
        } catch (IOException e) {
            // Log I/O errors (e.g., template not found) and return fallback.
            log.error("Error loading template '{}': {}", normalizedTemplateName, e.getMessage(), e);
            return "";
        } catch (TemplateException e) {
            // Log processing errors (e.g., syntax or data issues) and return fallback.
            log.error("Error processing template '{}'. Context: {}", normalizedTemplateName, model, e);
            return "";
        }
    }
}
