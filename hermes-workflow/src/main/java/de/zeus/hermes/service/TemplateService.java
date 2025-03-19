package de.zeus.hermes.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.Map;

/**
 * Service for rendering FreeMarker templates with data models.
 * <p>
 * This service processes FreeMarker templates by prioritizing the filesystem if the template exists there.
 * If not found, it falls back to loading from the JAR's configured template directory via {@link Configuration}.
 * It is used within {@link WorkflowEngine} to generate dynamic content such as emails, reports, or logs.
 * Errors are logged, and a fallback empty string is returned in case of failure.
 * </p>
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final Configuration freemarkerConfig;

    /**
     * Renders a FreeMarker template using the specified data model.
     * <p>
     * Attempts to load the template from the filesystem first, stripping any 'classpath:' prefix if present.
     * If not found, falls back to loading from the JAR via the FreeMarker {@link Configuration}.
     * The template is processed with the provided model and returned as a string.
     * Returns an empty string if the template name is invalid, the model is null, or rendering fails.
     * </p>
     *
     * @param templateName the name or path of the template file (e.g., "email.ftl" or "classpath:/templates/email.ftl")
     * @param model        the data model containing key-value pairs for template substitution, must not be null
     * @return the rendered template content as a string, or an empty string if an error occurs
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

        // Normalize the template name by removing 'classpath:' prefix
        final String normalizedTemplateName = templateName.replace("classpath:", "").replaceFirst("^/", "");

        // Step 1: Attempt to load from filesystem
        File file = new File(normalizedTemplateName);
        if (file.exists() && file.isFile()) {
            try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(file.toPath()));
                 StringWriter writer = new StringWriter()) {
                Template template = new Template(normalizedTemplateName, reader, freemarkerConfig);
                template.process(model, writer);
                log.info("Template '{}' rendered successfully from filesystem.", normalizedTemplateName);
                return writer.toString();
            } catch (IOException e) {
                log.warn("Failed to load template '{}' from filesystem: {}. Falling back to JAR.", normalizedTemplateName, e.getMessage());
            } catch (TemplateException e) {
                log.error("Error processing template '{}' from filesystem: {}", normalizedTemplateName, e.getMessage(), e);
                return "";
            }
        }

        // Step 2: Fallback to JAR via FreeMarker Configuration
        try {
            Template template = freemarkerConfig.getTemplate(normalizedTemplateName);
            try (StringWriter writer = new StringWriter()) {
                template.process(model, writer);
                log.info("Template '{}' rendered successfully from JAR.", normalizedTemplateName);
                return writer.toString();
            }
        } catch (IOException e) {
            log.error("Error loading template '{}' from JAR: {}", normalizedTemplateName, e.getMessage(), e);
            return "";
        } catch (TemplateException e) {
            log.error("Error processing template '{}' from JAR. Context: {}", normalizedTemplateName, model, e);
            return "";
        }
    }
}