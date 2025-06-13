package de.zeus.hermes.config;

import de.zeus.hermes.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.HashMap;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Custom YAML constructor for creating {@link WorkflowStep} instances.
 *
 * <p>
 * This constructor maps YAML properties to their corresponding {@link WorkflowStep} implementations.
 * It is responsible for parsing workflow step definitions from YAML configuration and ensuring
 * they are instantiated correctly.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
public class WorkflowStepConstructor extends Constructor {

    private static final Map<String, Class<? extends WorkflowStep>> STEP_MAPPING = new HashMap<>();

    static {
        STEP_MAPPING.put("executeSql", ExecuteSqlStep.class);
        STEP_MAPPING.put("fetchMailserver", FetchMailserverStep.class);
        STEP_MAPPING.put("fetchRecipients", FetchRecipientsStep.class);
        STEP_MAPPING.put("fetchContent", FetchContentStep.class);
        STEP_MAPPING.put("transform", TransformStep.class);
        STEP_MAPPING.put("applyTemplate", ApplyTemplateStep.class);
        STEP_MAPPING.put("sendEmail", SendEmailStep.class);
    }

    public WorkflowStepConstructor(Class<?> theRoot, LoaderOptions loaderOptions) {
        super(theRoot, loaderOptions);
    }

    @Override
    protected Object constructObject(Node node) {
        // Use standard construction for non-mapping nodes
        if (!(node instanceof MappingNode mappingNode)) {
            return super.constructObject(node);
        }

        // Extract YAML properties into a Map
        final Map<String, String> properties = extractProperties(mappingNode);

        // Retrieve step type and name from properties
        final String stepType = properties.get("step");
        final String stepName = properties.getOrDefault("name", "Unbenannter Step");

        if (!StringUtils.hasText(stepType)) {
            log.warn("No valid step type found. Using standard construction.");
            return super.constructObject(node);
        }

        // Get the corresponding WorkflowStep class; default to WorkflowStep.class if not found.
        final Class<? extends WorkflowStep> stepClass = STEP_MAPPING.getOrDefault(stepType, WorkflowStep.class);
        WorkflowStep stepInstance;
        try {
            // Create an instance via the default constructor (dependencies are injected by Spring)
            stepInstance = stepClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("Error instantiating step '{}': {}", stepType, e.getMessage(), e);
            return super.constructObject(node);
        }

        // Set the general properties of the step
        stepInstance.setName(stepName);

        // Set type-specific properties using pattern matching
        if (stepInstance instanceof ExecuteSqlStep sqlStep) {
            sqlStep.setQuery(properties.get("query"));
        } else if (stepInstance instanceof TransformStep transformStep) {
            transformStep.setType(properties.get("type"));
        } else if (stepInstance instanceof ApplyTemplateStep templateStep) {
            templateStep.setSubjectTemplate(properties.get("subjectTemplate"));
            templateStep.setBodyTemplate(properties.get("bodyTemplate"));
        } else if (stepInstance instanceof SendEmailStep emailStep) {
            emailStep.setSmtp(properties.get("smtp"));
        }

        log.info("Step created successfully: Type='{}', Name='{}', Properties={}", stepType, stepName, properties);
        return stepInstance;
    }

    /**
     * Extracts key-value pairs from a YAML MappingNode.
     *
     * @param mappingNode the YAML MappingNode to extract properties from.
     * @return a Map containing the extracted properties.
     */
    private Map<String, String> extractProperties(final MappingNode mappingNode) {
        final Map<String, String> properties = new HashMap<>();
        for (final NodeTuple tuple : mappingNode.getValue()) {
            final String key = ((ScalarNode) tuple.getKeyNode()).getValue();
            final String value = ((ScalarNode) tuple.getValueNode()).getValue();
            properties.put(key, value);
        }
        return properties;
    }
}
