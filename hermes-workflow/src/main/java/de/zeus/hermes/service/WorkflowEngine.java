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
 *
 * <p>
 * This class orchestrates the execution of {@link WorkflowStep} instances within a {@link Workflow},
 * managing dependencies and maintaining an execution context.
 * </p>
 *
 * @version 1.0.1
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
     * Executes a given workflow by processing its steps sequentially.
     *
     * <p>
     * Each step is executed within a shared execution context that provides access to required services.
     * If a step encounters an error, it is logged, and execution proceeds to the next step.
     * </p>
     *
     * @param workflow the {@link Workflow} to be executed; must not be null and must contain steps.
     */
    public void execute(final Workflow workflow) {
        // Validate input: Workflow and steps must be defined.
        if (workflow == null || workflow.getSteps() == null || workflow.getSteps().isEmpty()) {
            log.warn("No workflow or steps defined. Aborting execution.");
            return;
        }

        // Log the start of workflow execution.
        log.info("Starting workflow execution with {} steps...", workflow.getSteps().size());

        // Initialize execution context with required services.
        final Map<String, Object> context = new HashMap<>(Map.of(
                "sqlService", sqlService,
                "transformationService", transformationService,
                "templateService", templateService,
                "emailService", emailService
        ));

        boolean hasErrors = false;

        // Iterate through workflow steps and execute them sequentially.
        for (WorkflowStep step : workflow.getSteps()) {
            try {
                log.info("Executing step: Class = '{}' | Name = '{}' | Details = {}",
                        step.getClass().getSimpleName(),
                        step.getName(),
                        step);

                // Execute the step using the shared execution context.
                step.execute(context);

                log.info("Step '{}' executed successfully.", step.getName());
            } catch (Exception e) {
                log.error("Error executing step '{}': {}", step.getName(), e.getMessage(), e);
                hasErrors = true;
            }
        }

        // Log final execution status.
        log.info("Workflow execution completed. Errors encountered: {}", hasErrors);
    }
}
