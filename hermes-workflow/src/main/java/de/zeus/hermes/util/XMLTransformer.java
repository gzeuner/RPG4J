package de.zeus.hermes.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Utility class for transforming XML files using XSLT.
 */
@Slf4j
public final class XMLTransformer {

    // Private constructor to prevent instantiation.
    private XMLTransformer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Transforms an XML file using an XSLT file and outputs the result to a specified file.
     *
     * @param xmlFileName    the path to the XML file to be transformed.
     * @param xsltFileName   the path to the XSLT file defining the transformation rules.
     * @param outputFileName the path where the transformed result will be saved.
     */
    public static void transformXML(String xmlFileName, String xsltFileName, String outputFileName) {
        try {
            // Check if the XSLT file exists
            File xsltFile = new File(xsltFileName);
            if (!xsltFile.exists()) {
                log.error("XSLT file not found: {}", xsltFileName);
                return;
            }
            log.info("Loading XSLT file from: {}", xsltFileName);

            // Check if the XML file exists
            File xmlFile = new File(xmlFileName);
            if (!xmlFile.exists()) {
                log.error("XML file not found: {}", xmlFileName);
                return;
            }
            log.info("Loading XML file from: {}", xmlFileName);

            // Create a Transformer using the provided XSLT file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsltFile));

            // Prepare the XML source and output result
            Source xmlSource = new StreamSource(xmlFile);
            try (OutputStream os = new FileOutputStream(outputFileName)) {
                Result result = new StreamResult(os);
                transformer.transform(xmlSource, result);
                log.info("Transformation completed successfully. Output saved to: {}", outputFileName);
            }
        } catch (Exception e) {
            log.error("Error during XML transformation", e);
        }
    }
}
