package de.zeus.hermes.model;

import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Interface for all workflow steps.
 *
 * <p>
 * This interface defines the core methods required for executing a workflow step
 * and managing its name. Implementations of this interface are orchestrated by
 * {@link de.zeus.hermes.service.WorkflowEngine} to execute structured workflow operations.
 * </p>
 *
 * @version 1.0.1
 */
public interface WorkflowStep {

    /**
     * Executes the workflow step using the provided context.
     * The context may contain results or services from previous steps or the workflow engine,
     * enabling data sharing and service access during execution.
     *
     * @param context The workflow context containing relevant data and services.
     *                Must not be null.
     * @throws de.zeus.hermes.model.WorkflowStepException If the step execution fails due to an error.
     */
    void execute(Map<String, Object> context);

    /**
     * Retrieves the name of the workflow step.
     * The name is used for identification and logging purposes within the workflow.
     *
     * @return The name of the step, or null if not set.
     */
    String getName();

    /**
     * Sets the name of the workflow step.
     * This allows for dynamic naming of steps during workflow configuration.
     *
     * @param name The name to assign to the step. Should not be null or blank for meaningful identification.
     */
    void setName(String name);
}