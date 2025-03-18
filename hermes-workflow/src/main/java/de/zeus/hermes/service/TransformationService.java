package de.zeus.hermes.service;

import de.zeus.hermes.util.XMLTransformer;
import de.zeus.hermes.util.XmlExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Service for transforming data into XML format using XSLT and exporting it to a file.
 * <p>
 * This service handles exporting data to an intermediate XML file, applying an XSLT transformation,
 * and saving the final result to a designated output file.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransformationService {

    private final XmlExporter xmlExporter;

    @Value("${export.file-path}")
    private String exportPath;

    @Value("${export.xslt-path}")
    private String xsltPath;

    /**
     * Transforms a list of data into XML using an XSLT stylesheet and exports it to a file.
     * <p>
     * The process involves exporting the data to an intermediate XML file, applying the specified
     * XSLT transformation, and saving the result to the output file.
     * </p>
     *
     * @param data           the list of data maps to transform, where each map represents a row.
     * @param xsltType       the type of XSLT transformation to apply (e.g., "html", "csv").
     * @param outputFileName the name of the final output file.
     * @param columnTypes    a map defining the data types of columns for XML export.
     * @throws IllegalArgumentException if configuration (exportPath or xsltPath) is invalid.
     * @throws IOException              if file operations or XSLT transformation fail.
     */
    public void transform(final List<Map<String, Object>> data,
                          final String xsltType,
                          final String outputFileName,
                          final Map<String, String> columnTypes) {
        // Validate input data
        if (data == null || data.isEmpty()) {
            log.error("No data available for transformation.");
            return;
        }

        try {
            // Validate configuration properties
            if (exportPath == null || exportPath.isBlank()) {
                throw new IllegalArgumentException("Export path is not configured.");
            }
            if (xsltPath == null || xsltPath.isBlank()) {
                throw new IllegalArgumentException("XSLT path is not configured.");
            }

            // Determine the base export directory based on the exportPath value.
            // If the exportPath is specified as a classpath resource, default to a directory named "export" in the working directory.
            final Path baseExportDir = exportPath.startsWith("classpath:")
                    ? Paths.get(System.getProperty("user.dir"), "export")
                    : Paths.get(exportPath);

            // Create the export directory if it does not exist.
            if (!Files.exists(baseExportDir)) {
                Files.createDirectories(baseExportDir);
                log.info("Created base export directory: {}", baseExportDir.toAbsolutePath());
            }

            // Generate a unique intermediate XML filename.
            final String xmlFileName = "exported_data_" + System.currentTimeMillis() + ".xml";
            final Path xmlFilePath = baseExportDir.resolve(xmlFileName);

            // Export the data to an intermediate XML file.
            xmlExporter.exportListToXML(data, "root", xmlFilePath.toString(), columnTypes);

            // Build the XSLT filename based on the transformation type.
            final String xsltFileName = "xml_to_" + xsltType + ".xslt";
            final String resolvedXsltFilePath = getXsltFilePath(xsltFileName);

            // Define the final output file path.
            final Path outputFilePath = baseExportDir.resolve(outputFileName);

            // Apply the XSLT transformation to convert the intermediate XML to the desired format.
            XMLTransformer.transformXML(xmlFilePath.toString(), resolvedXsltFilePath, outputFilePath.toString());

            log.info("Transformation successful: {}", outputFilePath.toAbsolutePath());
        } catch (IllegalArgumentException e) {
            log.error("Configuration error: {}", e.getMessage());
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", e.getMessage());
        } catch (IOException e) {
            log.error("I/O error during transformation: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during transformation: {}", e.getMessage(), e);
        }
    }

    /**
     * Resolves the full path to an XSLT file, handling both classpath resources and filesystem paths.
     * <p>
     * For classpath resources, the XSLT file is copied to a temporary file, and its absolute path is returned.
     * </p>
     *
     * @param xsltFileName the name of the XSLT file (e.g., "xml_to_html.xslt").
     * @return the absolute path to the XSLT file.
     * @throws IOException if the XSLT file cannot be found or copied.
     */
    private String getXsltFilePath(final String xsltFileName) throws IOException {
        if (xsltPath.startsWith("classpath:")) {
            // Remove the "classpath:" prefix and any leading slash
            final String resourcePath = xsltPath.substring("classpath:".length()).replaceFirst("^/", "");
            final String fullResourcePath = "/" + resourcePath + "/" + xsltFileName;

            // Load the XSLT file as a resource from the classpath.
            try (final InputStream xsltStream = getClass().getResourceAsStream(fullResourcePath)) {
                if (xsltStream == null) {
                    throw new FileNotFoundException("XSLT resource not found: " + fullResourcePath);
                }

                // Create a temporary file for the XSLT content.
                final File tempXsltFile = File.createTempFile("tempXslt", ".xslt");
                tempXsltFile.deleteOnExit();

                // Copy the XSLT resource to the temporary file.
                Files.copy(xsltStream, tempXsltFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return tempXsltFile.getAbsolutePath();
            }
        }

        // For filesystem paths, simply resolve and return the path.
        return Paths.get(xsltPath, xsltFileName).toString();
    }
}
