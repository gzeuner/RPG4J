package de.zeus.hermes.model;

import de.zeus.hermes.config.DynamicPropertyResolver;
import de.zeus.hermes.service.SqlService;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
 * Implementation of {@link WorkflowStep} that executes an SQL query and stores its result in the workflow context.
 * <p>
 * The SQL query is resolved using dynamic properties. If the resolved query is empty or execution fails,
 * a fallback query is used.
 * </p>
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class ExecuteSqlStep implements WorkflowStep {

    /**
     * The name of this step, used for identification.
     */
    private String name;

    /**
     * The SQL query to be executed.
     */
    private String query;

    /**
     * An optional key used to store the SQL result in the workflow context.
     */
    private String contextKey;

    /**
     * Dynamic property resolver for resolving placeholders in the query.
     */
    @NonNull
    private final DynamicPropertyResolver propertyResolver;

    /**
     * SQL service to execute queries. This field is optional and can be injected via setter.
     */
    @Setter
    private SqlService sqlService;

    /**
     * Executes the SQL query and stores the result in the provided context.
     * The result is stored under a key determined by {@link #getResultKey()}.
     * <p>
     * If an error occurs or no data is returned, an appropriate fallback is stored.
     * </p>
     *
     * @param context the workflow context in which the SQL result is stored; must not be null.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        // Ensure context is not null
        Objects.requireNonNull(context, "Context must not be null");

        log.info("Starting ExecuteSqlStep - Name: '{}' | Query: '{}'", getName(), query);

        // Check if sqlService is available
        if (sqlService == null) {
            log.error("SqlService is not available for step '{}'. Aborting.", getName());
            context.put(getResultKey(), null);
            context.put(getResultKey() + "_error", "SqlService not injected");
            return;
        }

        // Resolve dynamic properties in the query; use a fallback query if the resolved query is empty.
        final String resolvedQuery = StringUtils.hasText(propertyResolver.resolve(query, "workflow.executeSql.defaultQuery"))
                ? propertyResolver.resolve(query, "workflow.executeSql.defaultQuery")
                : "SELECT 'Fallback' AS Wert FROM SYSIBM.SYSDUMMY1";

        if (!StringUtils.hasText(resolvedQuery)) {
            log.warn("Query for step '{}' is null or empty. Using fallback...", getName());
        }

        final String resultKey = getResultKey();
        try {
            // Execute the SQL query and obtain the list of results.
            final List<Map<String, Object>> results = sqlService.executeQueryForList(resolvedQuery);

            if (results.isEmpty()) {
                log.warn("No results found for query '{}'", resolvedQuery);
                context.put(resultKey, Collections.emptyList());
            } else {
                log.info("SQL result for step '{}': {} rows retrieved.", getName(), results.size());
                context.put(resultKey, results);
            }
        } catch (Exception e) {
            log.error("Error executing SQL for step '{}': {}", getName(), e.getMessage(), e);
            context.put(resultKey, null);
            context.put(resultKey + "_error", e.getMessage());
        }
    }

    /**
     * Returns the name of this step.
     * If the name is null or blank, a default name "Unbenannter Step" is returned.
     *
     * @return the step's name, or "Unbenannter Step" if not provided.
     */
    @Override
    public String getName() {
        return StringUtils.hasText(name) ? name : "Unbenannter Step";
    }

    /**
     * Determines the key used to store the SQL result in the workflow context.
     * If a specific contextKey is provided, it is used; otherwise, a default key is generated.
     *
     * @return the result key for storing the SQL result.
     */
    private String getResultKey() {
        return Optional.ofNullable(contextKey)
                .filter(StringUtils::hasText)
                .orElse("sqlResult_" + getName());
    }
}
