package de.zeus.hermes.service;

import de.zeus.hermes.model.Workflow;
import de.zeus.hermes.model.WorkflowStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Service responsible for executing workflows by processing their steps sequentially.
 * <p>
 * This class orchestrates the execution of {@link WorkflowStep} instances within a {@link Workflow},
 * managing dependencies and an execution context.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowEngine {

    private final SqlService sqlService;
    private final TransformationService transformationService;
    private final TemplateService templateService;
    private final EmailService emailService;

    /**
     * Executes a given workflow by iterating through its steps sequentially.
     * <p>
     * Each step is executed within a shared context that includes required services.
     * If a step fails, the error is logged and execution continues with the next step.
     * </p>
     *
     * @param workflow the {@link Workflow} to be executed; must not be null and must contain steps.
     */
    public void execute(final Workflow workflow) {
        // Validate input: workflow and its steps must be defined.
        if (workflow == null || workflow.getSteps() == null || workflow.getSteps().isEmpty()) {
            log.warn("No workflow or steps defined. Aborting execution.");
            return;
        }

        // Log the start of workflow execution.
        log.info("Starting workflow with {} steps...", workflow.getSteps().size());

        // Initialize the execution context with required services.
        final Map<String, Object> context = new HashMap<>(Map.of(
                "sqlService", sqlService,
                "transformationService", transformationService,
                "templateService", templateService,
                "emailService", emailService
        ));

        boolean hasErrors = false;

        // Process each step in the workflow sequentially.
        for (WorkflowStep step : workflow.getSteps()) {
            try {
                log.info("Executing step: Class = '{}' | Name = '{}' | Details = {}",
                        step.getClass().getSimpleName(),
                        step.getName(),
                        step);

                // Execute the step using the shared context.
                step.execute(context);

                log.info("Step '{}' executed successfully.", step.getName());
            } catch (Exception e) {
                log.error("Error executing step '{}': {}", step.getName(), e.getMessage(), e);
                hasErrors = true;
            }
        }

        // Log the final status of workflow execution.
        log.info("Workflow execution completed. Errors encountered: {}", hasErrors);
    }
}
