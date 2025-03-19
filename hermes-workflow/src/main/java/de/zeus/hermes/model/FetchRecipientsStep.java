package de.zeus.hermes.model;

import de.zeus.hermes.service.SqlService;
import de.zeus.hermes.config.DynamicPropertyResolver;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * A {@link WorkflowStep} implementation that fetches email recipients from a SQL query using {@link SqlService}.
 *
 * <p>
 * This step queries the database to retrieve recipient email addresses from the 'wert' column.
 * The retrieved addresses are stored in the workflow context under "email.recipients". If the query fails
 * or returns no valid recipients, a fallback to default recipients is applied. This step ensures
 * that email notifications are always sent to at least one recipient.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Data
@RequiredArgsConstructor
public class FetchRecipientsStep implements WorkflowStep {

    /**
     * The SQL query to fetch recipients.
     */
    private String query;

    /**
     * The name of this fetch recipients step, used for identification and logging.
     */
    private String name;

    /**
     * The service for executing SQL queries.
     */
    private final SqlService sqlService;

    /**
     * The resolver for dynamic configuration properties (e.g., fallback recipients).
     */
    private final DynamicPropertyResolver propertyResolver;

    /**
     * Executes the fetch recipients step by querying the database for recipient data.
     * <p>
     * Stores the results in the context under "email.recipients", or an error message under "email.recipients_error"
     * if the query fails. Uses fallback recipients if necessary.
     * </p>
     *
     * @param context the workflow context to store recipients; must not be null.
     */
    @Override
    public void execute(final Map<String, Object> context) {
        log.info("Starting FetchRecipientsStep - Name: '{}' | Query: '{}'", getName(), query);

        // Validate context
        if (context == null) {
            log.error("Context is null for step '{}'. Aborting email recipient fetch.", getName());
            return;
        }

        // Validate query input
        if (query == null || query.trim().isEmpty()) {
            log.warn("Query for step '{}' is null or empty. Using fallback recipients.", getName());
            setFallbackRecipients(context);
            return;
        }

        try {
            // Execute the SQL query to fetch recipient data
            final List<Map<String, Object>> results = sqlService.executeQueryForList(query);
            if (results.isEmpty()) {
                log.warn("No results returned for query '{}' in step '{}'. Using fallback recipients.", query, getName());
                setFallbackRecipients(context);
                return;
            }

            // Extract recipients from the 'wert' column
            final List<String> recipients = results.stream()
                    .map(row -> (String) row.get("wert"))
                    .filter(wert -> wert != null && !wert.trim().isEmpty())
                    .collect(Collectors.toList());

            if (recipients.isEmpty()) {
                log.warn("No valid recipients found in query results for step '{}'. Using fallback recipients.", getName());
                setFallbackRecipients(context);
            } else {
                // Store the recipients in the context for downstream steps
                context.put("email.recipients", recipients);
                log.info("Email recipients successfully fetched for step '{}': {}", getName(), recipients);
            }
        } catch (Exception e) {
            log.error("Error fetching recipients for step '{}': {}", getName(), e.getMessage(), e);
            context.put("email.recipients_error", e.getMessage());
            setFallbackRecipients(context);
        }
    }

    /**
     * Sets fallback recipients in the context based on a configuration property.
     * <p>
     * Uses "workflow.fetchRecipients.defaultRecipients" from application.yml as a comma-separated list.
     * </p>
     *
     * @param context the workflow context to store the fallback recipients.
     */
    private void setFallbackRecipients(final Map<String, Object> context) {
        final String fallbackRecipients = propertyResolver.resolve(null, "workflow.fetchRecipients.defaultRecipients");
        final List<String> recipients = Optional.ofNullable(fallbackRecipients)
                .filter(s -> !s.trim().isEmpty())
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(r -> !r.isEmpty())
                        .collect(Collectors.toList()))
                .orElseGet(() -> {
                    log.warn("No fallback recipients found in application.yml under 'workflow.fetchRecipients.defaultRecipients' for step '{}'.", getName());
                    return Collections.emptyList();
                });

        context.put("email.recipients", recipients);
        log.info("Set fallback email recipients for step '{}': {}", getName(), recipients);
    }

    /**
     * Retrieves the name of this fetch recipients step.
     * <p>
     * Returns a default name "Unnamed Step" if the configured name is null or blank.
     * </p>
     *
     * @return the step's name, or "Unnamed Step" if not set.
     */
    @Override
    public String getName() {
        return (name != null && !name.isBlank()) ? name : "Unnamed Step";
    }
}
