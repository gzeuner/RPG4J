package de.zeus.hermes.model;

import de.zeus.hermes.service.TemplateService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * WorkflowStep implementation that applies email templates using a TemplateService.
 * <p>
 * It renders the subject and body templates with the provided context data and stores the results back into the context.
 * </p>
 */
@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class ApplyTemplateStep implements WorkflowStep {

    /**
     * The name of this workflow step.
     */
    private String name;

    /**
     * The subject template file name.
     */
    private String subjectTemplate;

    /**
     * The body template file name.
     */
    private String bodyTemplate;

    /**
     * Service used to render templates.
     */
    private final TemplateService templateService;

    /**
     * Executes the template application step.
     * <p>
     * Renders both the subject and body templates using the provided context.
     * If no template is specified, default fallback templates are used.
     * The rendered subject and body are stored in the context.
     * </p>
     *
     * @param context the workflow context where the rendered templates are stored.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        log.info("Starting ApplyTemplateStep - Name: '{}' | Subject-Template: '{}' | Body-Template: '{}'",
                getName(), subjectTemplate, bodyTemplate);

        // Determine effective templates using fallback if necessary
        final String effectiveSubjectTemplate = StringUtils.hasText(subjectTemplate)
                ? subjectTemplate
                : "defaultSubject.ftl";
        if (!StringUtils.hasText(subjectTemplate)) {
            log.warn("No subject template specified for step '{}'. Using fallback: 'defaultSubject.ftl'", getName());
        }

        final String effectiveBodyTemplate = StringUtils.hasText(bodyTemplate)
                ? bodyTemplate
                : "defaultBody.ftl";
        if (!StringUtils.hasText(bodyTemplate)) {
            log.warn("No body template specified for step '{}'. Using fallback: 'defaultBody.ftl'", getName());
        }

        try {
            // Render the templates with the provided context
            final String emailSubject = templateService.renderTemplate(effectiveSubjectTemplate, context);
            final String emailBody = templateService.renderTemplate(effectiveBodyTemplate, context);

            // Store the rendered templates in the context
            context.put("emailSubject", emailSubject);
            context.put("emailBody", emailBody);

            // Truncate long content for logging purposes
            final String subjectLog = emailSubject.length() > 100 ? emailSubject.substring(0, 100) + "..." : emailSubject;
            final String bodyLog = emailBody.length() > 200 ? emailBody.substring(0, 200) + "..." : emailBody;

            log.info("Templates applied successfully for step '{}': Subject='{}', Body='{}'", getName(), subjectLog, bodyLog);
        } catch (Exception e) {
            log.error("Error rendering templates for step '{}': {}", getName(), e.getMessage(), e);
            context.put("template_error", e.getMessage());
        }
    }

    /**
     * Returns the name of this step.
     * If the name is null or blank, a default value "Unbenannter Step" is returned.
     *
     * @return the step's name or "Unbenannter Step" if not provided.
     */
    @Override
    public String getName() {
        return StringUtils.hasText(name) ? name : "Unbenannter Step";
    }
}
