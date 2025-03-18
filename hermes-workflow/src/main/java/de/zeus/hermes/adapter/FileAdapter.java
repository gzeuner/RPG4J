package de.zeus.hermes.adapter;

import de.zeus.hermes.config.WorkflowConfig;
import de.zeus.hermes.model.Workflow;
import lombok.RequiredArgsConstructor;
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
 * Implementation of the {@link FileConnectionPoint} interface.
 * <p>
 * This adapter uses {@link WorkflowConfig} to load YAML workflows from a file.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class FileAdapter implements FileConnectionPoint {

    private final WorkflowConfig workflowConfig;
    private final ResourceLoader resourceLoader;

    /**
     * Loads a workflow from the specified file path.
     *
     * @param filePath the path to the workflow file.
     * @return the loaded {@link Workflow}.
     * @throws IOException if the file is not found or an error occurs while reading the file.
     */
    @Override
    public Workflow loadWorkflow(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource(filePath);

        if (!resource.exists()) {
            throw new IOException("Workflow file not found: " + filePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return workflowConfig.loadWorkflow(inputStream);
        }
    }
}
