package de.zeus.hermes.model;

import de.zeus.hermes.service.SqlService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * A {@link WorkflowStep} implementation that fetches content from the database using a SQL query.
 * <p>
 * It retrieves data along with metadata via {@link SqlService} and stores the results in the provided context.
 * If the query is invalid or no data is returned, empty collections are stored.
 * </p>
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class FetchContentStep implements WorkflowStep {

    /**
     * SQL query used to fetch the content.
     */
    private String query;

    /**
     * Name of this step, used for identification and as a key in the workflow context.
     */
    private String name;

    /**
     * Service used to execute SQL queries.
     */
    private final SqlService sqlService;

    /**
     * Executes the content fetching step by running the SQL query and storing the result data and metadata
     * in the provided context.
     * <p>
     * If the query is empty or fails, the context is populated with empty data.
     * </p>
     *
     * @param context the workflow context where the fetched content and metadata are stored; must not be null.
     * @throws NullPointerException if the context is null.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        // Ensure the context is not null
        Objects.requireNonNull(context, "Context must not be null");

        log.info("Starting FetchContentStep - Name: '{}' | Query: '{}'", getName(), query);

        // Check if the query is valid (non-null and contains text)
        if (!StringUtils.hasText(query)) {
            log.warn("Query for step '{}' is null or empty. Aborting...", getName());
            context.put("sqlResult_fetchContent", Collections.emptyList());
            context.put("sqlResult_fetchContent_metadata", Collections.emptyMap());
            return;
        }

        try {
            // Execute the query and retrieve the result data along with metadata.
            final Map<String, Object> result = sqlService.executeQueryWithMetadata(query);

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("data");

            @SuppressWarnings("unchecked")
            final Map<String, String> metadata = (Map<String, String>) result.get("metadata");

            // Check if any results were returned.
            if (results.isEmpty()) {
                log.warn("No results for query '{}' in step '{}'.", query, getName());
                context.put("sqlResult_fetchContent", Collections.emptyList());
                context.put("sqlResult_fetchContent_metadata", Collections.emptyMap());
            } else {
                log.info("Content successfully fetched for step '{}': {} rows retrieved.", getName(), results.size());
                log.info("Metadata: {}", metadata);
                context.put("sqlResult_fetchContent", results);
                context.put("sqlResult_fetchContent_metadata", metadata);
            }
        } catch (Exception e) {
            // Log the error and store error details in the context.
            log.error("Error fetching content for step '{}': {}", getName(), e.getMessage(), e);
            context.put("sqlResult_fetchContent", null);
            context.put("sqlResult_fetchContent_metadata", null);
            context.put("content_error", e.getMessage());
        }
    }

    /**
     * Retrieves the name of this fetch content step.
     * <p>
     * Returns "Unnamed Step" if the configured name is null or blank.
     * </p>
     *
     * @return the step's name, or "Unnamed Step" if not provided.
     */
    @Override
    public String getName() {
        return StringUtils.hasText(name) ? name : "Unnamed Step";
    }
}
