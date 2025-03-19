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
 * Utility class for applying XSLT transformations to XML files.
 * This class provides a static method to transform XML content using an XSLT stylesheet
 * and save the output to a specified file.
 *
 * @version 1.0.1
 */
@Slf4j
public final class XMLTransformer {

    // Private constructor to prevent instantiation of utility class.
    private XMLTransformer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Applies an XSLT transformation to an XML file and writes the result to an output file.
     *
     * @param xmlFileName    Path to the input XML file.
     * @param xsltFileName   Path to the XSLT file defining transformation rules.
     * @param outputFileName Path to the file where the transformed output will be saved.
     */
    public static void transformXML(String xmlFileName, String xsltFileName, String outputFileName) {
        try {
            // Validate existence of XSLT file
            File xsltFile = new File(xsltFileName);
            if (!xsltFile.exists()) {
                log.error("XSLT file not found: {}", xsltFileName);
                return;
            }
            log.info("Loading XSLT file: {}", xsltFileName);

            // Validate existence of XML file
            File xmlFile = new File(xmlFileName);
            if (!xmlFile.exists()) {
                log.error("XML file not found: {}", xmlFileName);
                return;
            }
            log.info("Loading XML file: {}", xmlFileName);

            // Create a Transformer instance with the specified XSLT file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(xsltFile));

            // Define the XML input source and output destination
            Source xmlSource = new StreamSource(xmlFile);
            try (OutputStream os = new FileOutputStream(outputFileName)) {
                Result result = new StreamResult(os);
                transformer.transform(xmlSource, result);
                log.info("Transformation completed successfully. Output saved to: {}", outputFileName);
            }
        } catch (Exception e) {
            log.error("Unexpected error during XML transformation", e);
        }
    }
}