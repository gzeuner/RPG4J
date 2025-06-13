package de.zeus.hermes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Service for executing SQL queries using Spring's {@link JdbcTemplate}.
 *
 * <p>
 * This service provides methods to execute SQL queries and retrieve results
 * as lists, maps, or single-column values while supporting metadata extraction.
 * It is designed for integration with {@link WorkflowEngine}, ensuring errors
 * are handled gracefully by logging them and returning fallback values when necessary.
 * </p>
 *
 * @version 1.0.1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SqlService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Executes an SQL query and returns the result as a list of maps.
     * Each map represents a row with column names as keys and values as objects.
     *
     * @param query the SQL query to execute. Must not be null or blank.
     * @return a list of maps representing the query results, or an empty list if the query is invalid or fails.
     */
    public List<Map<String, Object>> executeQueryForList(final String query) {
        // Validate query input
        if (query == null || query.isBlank()) {
            log.warn("SQL query is empty or null.");
            return Collections.emptyList();
        }

        // Log the query execution attempt
        log.info("Executing SQL query: {}", query);

        try {
            final List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
            log.debug("Query returned {} rows.", result.size());
            return result;
        } catch (DataAccessException e) {
            // Log the error and return an empty list as fallback
            log.error("SQL query failed: {} - Error: {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Executes an SQL query and returns both the data and column metadata.
     * The result includes a list of rows ("data") and a map of column names to their types ("metadata").
     *
     * @param query the SQL query to execute. Must not be null or blank.
     * @return a map containing "data" (list of rows) and "metadata" (column types), or an empty map if the query fails.
     */
    public Map<String, Object> executeQueryWithMetadata(final String query) {
        // Validate query input
        if (query == null || query.isBlank()) {
            log.warn("SQL query for metadata is empty or null.");
            return Collections.emptyMap();
        }

        // Log the query execution attempt
        log.info("Executing SQL query with metadata: {}", query);

        final Map<String, Object> result = new HashMap<>();
        try {
            final List<Map<String, Object>> data = jdbcTemplate.query(query, (rs, rowNum) -> {
                final Map<String, Object> row = new HashMap<>();
                final ResultSetMetaData metaData = rs.getMetaData();

                // Extract metadata only for the first row.
                if (rowNum == 0) {
                    final Map<String, String> columnTypes = new HashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        columnTypes.put(metaData.getColumnLabel(i), metaData.getColumnTypeName(i));
                    }
                    result.put("metadata", columnTypes);
                }

                // Populate row data.
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                return row;
            });

            // Store the query results in the result map.
            result.put("data", data);
            log.debug("Query returned {} rows with metadata.", data.size());
            return result;
        } catch (DataAccessException e) {
            // Log the error and return an empty map as fallback.
            log.error("SQL query with metadata failed: {} - Error: {}", query, e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Executes an SQL query expecting a single-column result and returns it as a list of strings.
     *
     * @param query the SQL query to execute, expected to return one column. Must not be null or blank.
     * @return a list of strings from the first column, or an empty list if the query is invalid or fails.
     */
    public List<String> executeSingleColumnQuery(final String query) {
        // Validate query input
        if (query == null || query.isBlank()) {
            log.warn("Single-column SQL query is empty or null.");
            return Collections.emptyList();
        }

        // Log the query execution attempt
        log.info("Executing single-column SQL query: {}", query);

        try {
            final List<String> result = jdbcTemplate.queryForList(query, String.class);
            log.debug("Single-column query returned {} values.", result.size());
            return result;
        } catch (DataAccessException e) {
            // Log the error and return an empty list as fallback.
            log.error("SQL single-column query failed: {} - Error: {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
