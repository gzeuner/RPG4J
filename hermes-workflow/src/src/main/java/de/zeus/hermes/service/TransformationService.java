package de.zeus.hermes.service;

import de.zeus.hermes.util.XmlExporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

/**
 * Service for transforming data into XML format using Apache Camel XSLT routes.
 *
 * @version 2.0 (Camel-powered)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransformationService {

    private final XmlExporter xmlExporter;

    @Autowired
    private CamelContext camelContext;

    @Value("${export.file-path}")
    private String exportPath;

    /**
     * Transforms a list of data into the desired output format using Apache Camel and XSLT.
     *
     * @param data           The list of data maps to transform.
     * @param xsltType       The XSLT type (e.g. csv, html, json...).
     * @param outputFileName Name of the final output file.
     * @param columnTypes    Column type metadata for XML generation.
     */
    public void transform(final List<Map<String, Object>> data,
                          final String xsltType,
                          final String outputFileName,
                          final Map<String, String> columnTypes) {

        if (data == null || data.isEmpty()) {
            log.error("No data available for transformation.");
            return;
        }

        try {
            // Validate export path
            if (exportPath == null || exportPath.isBlank()) {
                throw new IllegalArgumentException("Export path is not configured.");
            }

            // Create export directory if missing
            final Path baseExportDir = Paths.get(exportPath);
            if (!Files.exists(baseExportDir)) {
                Files.createDirectories(baseExportDir);
                log.info("Created export directory: {}", baseExportDir.toAbsolutePath());
            }

            // Create intermediate XML
            final String xmlFileName = "exported_data_" + System.currentTimeMillis() + ".xml";
            final Path xmlFilePath = baseExportDir.resolve(xmlFileName);
            xmlExporter.exportListToXML(data, "root", xmlFilePath.toString(), columnTypes);
            log.info("XML file created for transformation: {}", xmlFilePath);

            // Prepare Camel call
            final ProducerTemplate template = camelContext.createProducerTemplate();
            try (InputStream xmlInput = Files.newInputStream(xmlFilePath)) {
                template.sendBodyAndHeader(
                        "direct:transform-" + xsltType,
                        xmlInput,
                        Exchange.FILE_NAME,
                        outputFileName
                );
            }

            log.info("Camel XSLT transformation complete: {}", outputFileName);
        } catch (IllegalArgumentException e) {
            log.error("Configuration error: {}", e.getMessage());
        } catch (IOException e) {
            log.error("I/O error during transformation: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during Camel transformation: {}", e.getMessage(), e);
        }
    }
}
