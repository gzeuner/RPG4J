package de.zeus.hermes.camel;

import de.zeus.hermes.adapter.FileConnectionPoint;
import de.zeus.hermes.model.Workflow;
import de.zeus.hermes.service.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.springframework.stereotype.Component;

/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Defines a Camel route for processing workflow YAML files from the hot folder.
 * <p>
 * This class configures a route that:
 * <ul>
 *     <li>Monitors the configured hot folder for YAML files</li>
 *     <li>Loads the workflow using the file adapter</li>
 *     <li>Triggers workflow execution via the WorkflowEngine</li>
 * </ul>
 * </p>
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowRoute extends RouteBuilder {

    private final WorkflowEngine workflowEngine;
    private final FileConnectionPoint fileAdapter;

    @Override
    public void configure() {
        // Define the route from the file endpoint using the configured hot folder.
        from("file://{{workflow.hot-folder}}?noop=true&include=.*\\.yaml")
                // Process the file: load the workflow from the file and set it as the message body.
                .process(exchange -> {
                    GenericFile<?> file = exchange.getIn().getBody(GenericFile.class);
                    log.info("New workflow file found: {}", file.getFileName());
                    String filePath = file.getAbsoluteFilePath();
                    Workflow workflow = fileAdapter.loadWorkflow(filePath);
                    exchange.getIn().setBody(workflow);
                })
                // Process the workflow: execute the loaded workflow.
                .process(exchange -> {
                    Workflow workflow = exchange.getIn().getBody(Workflow.class);
                    log.info("Starting workflow execution: {}", workflow);
                    workflowEngine.execute(workflow);
                });
    }
}
