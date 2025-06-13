package de.zeus.hermes.model;

import de.zeus.hermes.service.SqlService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

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
 * A {@link WorkflowStep} implementation that fetches a mail server address from a SQL query using {@link SqlService}.
 *
 * <p>
 * This step queries the database to retrieve a mail server address and stores it in the workflow context
 * under the step's name. If the SQL query fails or returns an invalid result, a fallback value from the
 * application configuration is used, ensuring robust execution.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class FetchMailserverStep implements WorkflowStep {

    private static final String FALLBACK_PROPERTY = "mail.host"; // Fallback SMTP property key from configuration

    /**
     * The SQL query to fetch the mail server address.
     */
    private String query;

    /**
     * The name of this fetch mail server step, used for identification, logging, and as a context key.
     */
    private String name;

    /**
     * Service for executing SQL queries.
     */
    private final SqlService sqlService;

    /**
     * Spring Environment for resolving configuration properties.
     */
    private final Environment environment;

    /**
     * Executes this workflow step by querying the database for a mail server address.
     * The result is stored in the provided context under the step's name.
     * If the query fails or yields no valid data, a fallback mail server is used.
     *
     * @param context the workflow context to store the mail server; must not be null.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        // Ensure the context is not null.
        Objects.requireNonNull(context, "Context must not be null for step '" + getName() + "'");

        log.info("Starting FetchMailserverStep - Name: '{}', Query: '{}'", getName(), query);

        // Validate query and sqlService.
        if (!StringUtils.hasText(query)) {
            log.warn("Query for step '{}' is null or empty. Using fallback mail server.", getName());
            setFallbackMailServer(context);
            return;
        }
        if (sqlService == null) {
            log.warn("SqlService is not available for step '{}'. Using fallback mail server.", getName());
            setFallbackMailServer(context);
            return;
        }

        try {
            // Execute the query to fetch mail server data.
            final List<Map<String, Object>> results = sqlService.executeQueryForList(query);
            handleQueryResults(context, results);
        } catch (Exception e) {
            log.error("Error fetching mail server for step '{}': {}", getName(), e.getMessage(), e);
            context.put(getName() + "_error", e.getMessage());
            setFallbackMailServer(context);
        }
    }

    /**
     * Processes the query results and stores the mail server in the context.
     * If no valid result is found, the fallback mail server is used.
     *
     * @param context the workflow context to store the result.
     * @param results the list of query results from SqlService.
     */
    private void handleQueryResults(final Map<String, Object> context, final List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            log.warn("No results for query '{}' in step '{}'. Using fallback mail server.", query, getName());
            setFallbackMailServer(context);
        } else {
            final Map<String, Object> result = results.get(0);
            if (results.size() > 1) {
                log.warn("Multiple results for query '{}' in step '{}'. Using first result: {}", query, getName(), result);
            }
            setMailServerFromResult(context, result);
        }
    }

    /**
     * Extracts the mail server address from the query result and stores it in the context.
     * If no valid mail server is found, falls back to the fallback mail server.
     *
     * @param context the workflow context to store the mail server.
     * @param result  the query result map containing mail server data.
     */
    private void setMailServerFromResult(final Map<String, Object> context, final Map<String, Object> result) {
        final String mailServer = Optional.ofNullable(result.get("KEWALP"))
                .map(Object::toString)
                .filter(StringUtils::hasText)
                .orElseGet(() -> {
                    log.warn("No valid mail server in result for step '{}'. Using fallback.", getName());
                    return null;
                });

        if (mailServer != null) {
            context.put(getName(), mailServer);
            log.info("Mail server successfully fetched for step '{}': {}", getName(), mailServer);
        } else {
            setFallbackMailServer(context);
        }
    }

    /**
     * Sets a fallback mail server in the context based on the "mail.host" property from configuration.
     *
     * @param context the workflow context to store the fallback mail server.
     */
    private void setFallbackMailServer(final Map<String, Object> context) {
        final String fallback = Optional.ofNullable(environment)
                .map(env -> env.getProperty(FALLBACK_PROPERTY))
                .filter(StringUtils::hasText)
                .orElse(null);

        context.computeIfAbsent(getName(), key -> {
            if (fallback != null) {
                log.info("Using fallback mail server from configuration for step '{}': {}", getName(), fallback);
                return fallback;
            } else {
                log.warn("No fallback mail server found in configuration for property '{}' in step '{}'", FALLBACK_PROPERTY, getName());
                return null;
            }
        });
    }

    /**
     * Retrieves the name of this fetch mail server step.
     * Returns a default name ("UnnamedStep") if the configured name is null or blank.
     *
     * @return the step's name, or "UnnamedStep" if not set.
     */
    @Override
    public String getName() {
        return Optional.ofNullable(name)
                .filter(StringUtils::hasText)
                .orElse("UnnamedStep");
    }
}
