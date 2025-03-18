package de.zeus.hermes.model;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * An exception thrown during the execution of a {@link WorkflowStep} to indicate a failure.
 * This exception wraps a specific error message and an underlying cause, allowing detailed error reporting
 * within a workflow execution context, such as in {@link WorkflowEngine}.
 */
public class WorkflowStepException extends RuntimeException {

    /**
     * Constructs a new {@code WorkflowStepException} with the specified message and cause.
     *
     * @param message The detail message explaining the reason for the exception.
     * @param cause   The underlying cause of the exception (e.g., an IOException or SQLException).
     */
    public WorkflowStepException(String message, Throwable cause) {
        super(message, cause);
        // Pass the message and cause to the parent RuntimeException constructor
    }
}