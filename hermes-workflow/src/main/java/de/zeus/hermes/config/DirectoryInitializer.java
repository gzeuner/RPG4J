package de.zeus.hermes.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Configuration component to initialize hotfolder and processed directories.
 * <p>
 * Ensures that the hotfolder and processed directories exist in the filesystem
 * upon application startup. Creates them if they are missing.
 * </p>
 *
 * @author gzeuner
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryInitializer {

    @Value("${workflow.hot-folder}")
    private String hotFolderPath;

    @Value("${workflow.processed-folder}")
    private String processedFolderPath;

    /**
     * Initializes the hotfolder and processed directories when the application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDirectories() {
        createDirectoryIfNotExists(hotFolderPath, "hotfolder");
        createDirectoryIfNotExists(processedFolderPath, "processed");
    }

    private void createDirectoryIfNotExists(String path, String name) {
        File directory = new File(path);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                log.info("{} directory created at: {}", name, path);
            } else {
                log.error("Failed to create {} directory at: {}", name, path);
            }
        } else {
            log.info("{} directory already exists at: {}", name, path);
        }
    }
}