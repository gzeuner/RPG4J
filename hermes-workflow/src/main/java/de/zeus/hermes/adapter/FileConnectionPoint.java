package de.zeus.hermes.adapter;

import de.zeus.hermes.model.Workflow;
import java.io.IOException;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Interface for processing workflow files.
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
public interface FileConnectionPoint {

    /**
     * Loads a workflow from the specified file path.
     *
     * @param filePath the path to the workflow file.
     * @return the loaded workflow.
     * @throws IOException if an error occurs while reading the file.
     */
    Workflow loadWorkflow(String filePath) throws IOException;
}
