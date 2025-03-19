package de.zeus.hermes.factory;

import de.zeus.hermes.config.DynamicPropertyResolver;
import de.zeus.hermes.model.*;
import de.zeus.hermes.service.EmailService;
import de.zeus.hermes.service.SqlService;
import de.zeus.hermes.service.TransformationService;
import de.zeus.hermes.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Factory for creating {@link WorkflowStep} instances.
 *
 * <p>
 * This factory dynamically generates different types of workflow steps based on the provided step type
 * and configures them with the given properties. It ensures that steps are properly initialized
 * before being executed within a workflow managed by {@link de.zeus.hermes.service.WorkflowEngine}.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStepFactory {

    private final SqlService sqlService;
    private final DynamicPropertyResolver propertyResolver;
    private final TransformationService transformationService;
    private final TemplateService templateService;
    private final EmailService emailService;
    private final Environment environment;

    /**
     * Creates a WorkflowStep instance based on the provided step type and properties.
     *
     * @param stepType   the type of the workflow step to create.
     * @param properties a map of properties used to configure the workflow step.
     * @return the configured WorkflowStep.
     * @throws IllegalArgumentException if the stepType is unknown.
     */
    public WorkflowStep createWorkflowStep(final String stepType, final Map<String, Object> properties) {
        // Create a workflow step instance based on the step type using a switch expression.
        final WorkflowStep step = switch (stepType) {
            case "fetchMailserver" -> new FetchMailserverStep(sqlService, environment);
            case "fetchRecipients" -> new FetchRecipientsStep(sqlService, propertyResolver);
            case "fetchContent" -> new FetchContentStep(sqlService);
            case "executeSql" -> new ExecuteSqlStep(propertyResolver);
            case "transform" -> new TransformStep(transformationService);
            case "applyTemplate" -> new ApplyTemplateStep(templateService);
            case "sendEmail" -> new SendEmailStep(emailService, environment);
            default -> throw new IllegalArgumentException("Unknown step type: " + stepType);
        };

        // Set general properties for the step.
        final String stepName = (String) properties.getOrDefault("name", "Unbenannter Step");
        step.setName(stepName);

        // Set type-specific properties using pattern matching.
        if (step instanceof ExecuteSqlStep sqlStep) {
            sqlStep.setQuery((String) properties.get("query"));
            sqlStep.setContextKey((String) properties.get("contextKey"));
            sqlStep.setSqlService(sqlService);
        } else if (step instanceof FetchMailserverStep mailserverStep) {
            mailserverStep.setQuery((String) properties.get("query"));
        } else if (step instanceof FetchRecipientsStep recipientsStep) {
            recipientsStep.setQuery((String) properties.get("query"));
        } else if (step instanceof FetchContentStep contentStep) {
            contentStep.setQuery((String) properties.get("query"));
        } else if (step instanceof TransformStep transformStep) {
            final String type = (String) properties.get("type");
            transformStep.setType(propertyResolver.resolve(type, "workflow.default.transform.type"));
        } else if (step instanceof ApplyTemplateStep templateStep) {
            templateStep.setSubjectTemplate((String) properties.get("subjectTemplate"));
            templateStep.setBodyTemplate((String) properties.get("bodyTemplate"));
        } else if (step instanceof SendEmailStep emailStep) {
            emailStep.setSmtp((String) properties.get("smtp"));
        }

        log.info("Workflow step created: Type='{}', Name='{}', Properties={}", stepType, stepName, properties);
        return step;
    }
}
