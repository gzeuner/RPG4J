package de.zeus.hermes.model;

import de.zeus.hermes.service.TransformationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * A {@link WorkflowStep} implementation that transforms data using {@link TransformationService}.
 *
 * <p>
 * This step retrieves SQL query results from the execution context, applies a transformation
 * based on the specified transformation type, and stores the output filename in the context
 * for use in subsequent workflow steps.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class TransformStep implements WorkflowStep {

    /**
     * The name of this transformation step, used for identification and logging.
     */
    private String name;

    /**
     * The type of transformation to apply (e.g., "xml", "csv"), determining the XSLT to use.
     */
    private String type;

    /**
     * The service responsible for performing the data transformation.
     */
    private final TransformationService transformationService;

    /**
     * Executes the transformation step by processing SQL result data from the context.
     * <p>
     * If the transformation is successful, the output filename is stored in the context under "transformedContent".
     * If an error occurs, an error message is stored under "transformedContent_error".
     * </p>
     *
     * @param context the workflow context containing SQL results and metadata; must not be null.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        log.info("Starting TransformStep - Name: '{}' | Type: '{}'", getName(), type);

        final Object resultData = context.get("sqlResult_fetchContent");
        if (resultData == null) {
            log.error("Error: No result data found in context for step '{}'.", getName());
            return;
        }

        try {
            // Validate transformation type.
            if (type == null || type.isBlank()) {
                log.error("Error: Transformation type is null or blank for step '{}'.", getName());
                context.put("transformedContent_error", "Transformation type is invalid");
                return;
            }

            final String outputFileName = "exported_data." + type;

            // Process result data if it's a list.
            if (resultData instanceof List<?> list) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> resultList = (List<Map<String, Object>>) list;

                @SuppressWarnings("unchecked")
                final Map<String, String> columnTypes = (Map<String, String>) context.get("sqlResult_fetchContent_metadata");
                if (columnTypes == null) {
                    log.warn("No metadata found in context for step '{}'. Proceeding without type information.", getName());
                }

                // Perform the transformation using the transformation service.
                transformationService.transform(resultList, type, outputFileName, columnTypes);

                // Store the output filename in the context for downstream steps.
                context.put("transformedContent", outputFileName);
                log.info("Transformation completed for step '{}': {}", getName(), outputFileName);
            } else {
                log.error("Error: Result data is not a List<Map<String, Object>> for step '{}'. Actual type: {}",
                        getName(), resultData.getClass().getSimpleName());
                context.put("transformedContent_error", "Invalid result data type");
            }
        } catch (Exception e) {
            log.error("Error during transformation for step '{}': {}", getName(), e.getMessage(), e);
            context.put("transformedContent_error", e.getMessage());
        }
    }

    /**
     * Retrieves the name of this transformation step.
     * Returns a default name "Unnamed Step" if the configured name is null or blank.
     *
     * @return the step's name, or "Unnamed Step" if not set.
     */
    @Override
    public String getName() {
        return (name != null && !name.isBlank()) ? name : "Unnamed Step";
    }
}
