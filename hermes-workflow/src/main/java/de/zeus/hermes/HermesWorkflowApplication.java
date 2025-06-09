/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

package de.zeus.hermes;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Hermes Workflow application.
 * Initializes and starts the Spring Boot application.
 *
 * @author gzeuner (https://tiny-tool.de)
 * @version 1.0.1
 */
@SpringBootApplication
@EnableEncryptableProperties
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
