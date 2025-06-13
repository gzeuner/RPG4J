package de.zeus.hermes.adapter;

import de.zeus.hermes.config.WorkflowConfig;
import de.zeus.hermes.model.Workflow;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Implementation of the {@link FileConnectionPoint} interface.
 * <p>
 * This adapter uses {@link WorkflowConfig} to load YAML workflows from either the filesystem or the JAR.
 * It prioritizes loading from the filesystem if the file exists, falling back to the JAR's resources/workflows directory.
 * </p>
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
@Component
@RequiredArgsConstructor
public class FileAdapter implements FileConnectionPoint {

    private final WorkflowConfig workflowConfig;
    private final ResourceLoader resourceLoader;

    @Override
    public Workflow loadWorkflow(String filePath) throws IOException {
        // Remove ‘classpath:’ for file system check
        String cleanFilePath = filePath.replace("classpath:", "").replaceFirst("^/", "");

        // Step 1: Check the file system first
        File file = new File(cleanFilePath);
        if (file.exists() && file.isFile()) {
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                return workflowConfig.loadWorkflow(inputStream);
            }
        }

        // Step 2: Fallback to JAR resource
        String classpathPath = "classpath:workflows/" + Paths.get(cleanFilePath).getFileName().toString();
        Resource resource = resourceLoader.getResource(classpathPath);

        if (!resource.exists()) {
            throw new IOException("Workflow file not found in filesystem or JAR: " + filePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return workflowConfig.loadWorkflow(inputStream);
        }
    }
}