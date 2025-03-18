package de.zeus.hermes.runner;

import de.zeus.hermes.config.WorkflowConfig;
import de.zeus.hermes.model.Workflow;
import de.zeus.hermes.service.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * A Spring Boot {@link CommandLineRunner} implementation that loads and executes a workflow upon application startup.
 * <p>
 * The workflow path can be specified via command-line arguments or a default configuration property.
 * This runner handles resource loading, workflow execution, and error logging.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowCommandLineRunner implements CommandLineRunner {

    private final WorkflowEngine workflowEngine;
    private final WorkflowConfig workflowConfig;
    private final ResourceLoader resourceLoader;

    @Value("${workflow.default-workflow}")
    private String defaultWorkflowPath;

    /**
     * Executes the workflow loading and processing logic when the application starts.
     * <p>
     * The workflow path is determined from the first command-line argument if provided; otherwise, it falls back to the configured default.
     * The method loads the workflow resource, processes it, and then executes the workflow.
     * </p>
     *
     * @param args command-line arguments passed to the application; the first argument, if provided, is used as the workflow path.
     */
    @Override
    public void run(final String... args) {
        log.info("Starting WorkflowCommandLineRunner...");

        // Determine the workflow path from command-line arguments or fallback to the default workflow path.
        final String workflowPath = (args != null && args.length > 0) ? args[0] : defaultWorkflowPath;
        log.info("Loading workflow from: {}", workflowPath);

        try {
            // Load the workflow resource using the ResourceLoader.
            final Resource resource = resourceLoader.getResource(workflowPath);

            // Check if the resource exists; throw an exception if it doesn't.
            if (!resource.exists()) {
                throw new IOException(String.format("Workflow file not found: %s", workflowPath));
            }

            log.info("Found workflow resource: {}", resource.getURI());

            // Load and execute the workflow using a try-with-resources block to ensure the InputStream is closed.
            try (final InputStream inputStream = resource.getInputStream()) {
                final Workflow workflow = workflowConfig.loadWorkflow(inputStream);
                workflowEngine.execute(workflow);
            }

        } catch (IOException e) {
            // Log IO-specific errors with stack trace for debugging.
            log.error("IO error while loading workflow '{}': {}", workflowPath, e.getMessage(), e);
        } catch (Exception e) {
            // Catch any unexpected errors during workflow processing and log them with stack trace.
            log.error("Unexpected error while processing workflow '{}': {}", workflowPath, e.getMessage(), e);
        }
    }
}
