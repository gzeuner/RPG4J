package de.zeus.hermes.camel;

import de.zeus.hermes.adapter.FileConnectionPoint;
import de.zeus.hermes.model.Workflow;
import de.zeus.hermes.service.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Camel route for processing workflow YAML files from the hotfolder using a timer.
 * <p>
 * Periodically scans the configured hotfolder for YAML files, loads and executes the workflow,
 * and moves the processed file to the processed folder with a timestamp.
 * </p>
 *
 * @author gzeuner
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowRoute extends RouteBuilder {

    private final FileConnectionPoint fileAdapter;
    private final WorkflowEngine workflowEngine;

    @Value("${workflow.hot-folder}")
    private String hotFolder;

    @Value("${workflow.processed-folder}")
    private String processedFolder;

    @Override
    public void configure() {
        log.info("Configuring Camel timer route for hotfolder: {}", hotFolder);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        from("timer:scanHotfolder?period=5000")
                .routeId("hotfolderTimerRoute")
                .process(exchange -> {
                    log.debug("Timer triggered, scanning hotfolder: {}", hotFolder);
                    File hotFolderDir = new File(hotFolder);
                    if (!hotFolderDir.exists() || !hotFolderDir.isDirectory()) {
                        log.warn("Hotfolder '{}' does not exist or is not a directory.", hotFolder);
                        return;
                    }

                    File[] yamlFiles = hotFolderDir.listFiles((dir, name) -> {
                        boolean matches = name.toLowerCase().endsWith(".yaml");
                        log.debug("Checking file: {}, matches .yaml: {}", name, matches);
                        return matches;
                    });
                    if (yamlFiles == null || yamlFiles.length == 0) {
                        log.debug("No YAML files found in hotfolder '{}'.", hotFolder);
                        return;
                    }

                    for (File file : yamlFiles) {
                        String filePath = file.getAbsolutePath();
                        log.info("Processing workflow file: {}", filePath);

                        try {
                            Workflow workflow = fileAdapter.loadWorkflow(filePath);
                            workflowEngine.execute(workflow);

                            String fileName = Paths.get(filePath).getFileName().toString();
                            String timestamp = dateFormat.format(new Date());
                            String processedFileName = fileName.replace(".yaml", "_" + timestamp + ".yaml");
                            String processedFilePath = Paths.get(processedFolder, processedFileName).toString();

                            File processedFile = new File(processedFilePath);
                            if (file.renameTo(processedFile)) {
                                log.info("Moved processed file to: {}", processedFilePath);
                            } else {
                                log.error("Failed to move file to: {}", processedFilePath);
                            }
                        } catch (Exception e) {
                            log.error("Error processing workflow file '{}': {}", filePath, e.getMessage(), e);
                        }
                    }
                });
    }
}