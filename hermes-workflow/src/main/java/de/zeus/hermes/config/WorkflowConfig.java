package de.zeus.hermes.config;

import de.zeus.hermes.factory.WorkflowStepFactory;
import de.zeus.hermes.model.Workflow;
import de.zeus.hermes.model.WorkflowStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Configuration class for loading workflows from a YAML file.
 * <p>
 * This class parses the YAML input stream and creates a {@link Workflow} instance
 * by constructing each {@link WorkflowStep} using the {@link WorkflowStepFactory}.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowConfig {

    private final ResourceLoader resourceLoader;
    private final DynamicPropertyResolver propertyResolver;
    private final WorkflowStepFactory workflowStepFactory;

    /**
     * Loads a {@link Workflow} from the provided YAML input stream.
     *
     * @param inputStream the input stream of the YAML workflow configuration.
     * @return a {@link Workflow} containing the list of workflow steps.
     * @throws IOException if an I/O error occurs during reading the input stream.
     */
    public Workflow loadWorkflow(final InputStream inputStream) throws IOException {
        // Parse the YAML input stream
        final Yaml yaml = new Yaml();
        final Map<String, Object> root = yaml.load(inputStream);

        // Extract the 'steps' list from the YAML root
        final List<Map<String, Object>> stepsList = (List<Map<String, Object>>) root.get("steps");
        final List<WorkflowStep> steps = new ArrayList<>();

        if (stepsList == null) {
            log.warn("No 'steps' entry found in the workflow configuration.");
            return new Workflow(steps);
        }

        // Process each step defined in the YAML configuration
        for (final Map<String, Object> stepMap : stepsList) {
            final String stepType = (String) stepMap.get("step");
            log.info("Loading step: {}", stepType);
            log.debug("Original properties: {}", stepMap);

            // Resolve dynamic properties for the workflow step
            resolveWorkflowProperties(stepMap, stepType);

            try {
                final WorkflowStep step = workflowStepFactory.createWorkflowStep(stepType, stepMap);
                steps.add(step);
            } catch (Exception e) {
                log.error("Error creating step '{}': {}", stepType, e.getMessage(), e);
            }
        }

        return new Workflow(steps);
    }

    /**
     * Resolves dynamic properties for the workflow step.
     * <p>
     * It processes specific keys like 'query' and 'smtp' by resolving them via the property resolver.
     * </p>
     *
     * @param stepMap  the map containing properties for the workflow step.
     * @param stepType the type of the workflow step.
     */
    private void resolveWorkflowProperties(final Map<String, Object> stepMap, final String stepType) {
        // Resolve the 'query' property if present
        stepMap.computeIfPresent("query", (k, v) ->
                resolveWithLogging((String) v, "workflow." + stepType + ".query"));
        // Resolve the 'smtp' property if present
        stepMap.computeIfPresent("smtp", (k, v) ->
                resolveWithLogging((String) v, "mail.host"));
    }

    /**
     * Resolves a property using the {@link DynamicPropertyResolver} and logs the resolution.
     *
     * @param originalValue the original property value.
     * @param propertyKey   the key used for property resolution.
     * @return the resolved property value, or "N/A" if the resolved value is null.
     */
    private String resolveWithLogging(final String originalValue, final String propertyKey) {
        final String resolvedValue = propertyResolver.resolve(originalValue, propertyKey);
        log.info("Resolved property '{}' -> '{}'", propertyKey, resolvedValue != null ? resolvedValue : "N/A");
        return resolvedValue;
    }
}
