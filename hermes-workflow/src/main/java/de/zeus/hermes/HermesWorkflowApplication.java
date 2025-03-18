/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Entry point for the Hermes Workflow application.
 * This class initializes and starts the Spring Boot application.
 *
 * @author gzeuner (https://tiny-tool.de)
 * @since 1.0
 */
package de.zeus.hermes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HermesWorkflowApplication {

    /**
     * Main method to launch the Spring Boot application.
     *
     * @param args Command-line arguments passed at runtime.
     */
    public static void main(String[] args) {
        SpringApplication.run(HermesWorkflowApplication.class, args);
    }
}
