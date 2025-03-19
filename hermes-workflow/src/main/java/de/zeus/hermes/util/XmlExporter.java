package de.zeus.hermes.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;
import java.util.Map;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Utility class for exporting data as an XML file.
 * This class provides functionality to convert a list of data entries into an XML document
 * and store it as a file, optionally including data type attributes.
 *
 * @version 1.0.1
 */
@Slf4j
@Component
public class XmlExporter {

    /**
     * Converts a list of data entries into an XML file and saves it to the specified location.
     *
     * @param data            List of data entries, each represented as a map where keys are column names.
     * @param rootElementName Name of the root element in the generated XML document.
     * @param xmlFilePath     Destination file path for the XML output.
     * @param columnTypes     Optional map defining the data types of the columns, used as attributes.
     */
    public void exportListToXML(final List<Map<String, Object>> data,
                                final String rootElementName,
                                final String xmlFilePath,
                                final Map<String, String> columnTypes) {
        try {
            // Initialize a DocumentBuilderFactory and create a new document
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            // Create the root element and append it to the document
            final Element root = doc.createElement(rootElementName);
            doc.appendChild(root);

            // Iterate over each data entry and add it to the XML structure
            for (final Map<String, Object> row : data) {
                final Element rowElement = doc.createElement("row");

                for (final Map.Entry<String, Object> entry : row.entrySet()) {
                    final String key = entry.getKey();
                    final String value = entry.getValue() != null ? entry.getValue().toString() : "";
                    final Element columnElement = doc.createElement(key);
                    columnElement.appendChild(doc.createTextNode(value));

                    // If column types are specified, add them as attributes in uppercase
                    if (columnTypes != null && columnTypes.containsKey(key)) {
                        columnElement.setAttribute("type", columnTypes.get(key).toUpperCase());
                    }
                    rowElement.appendChild(columnElement);
                }
                root.appendChild(rowElement);
            }

            // Configure transformer to format the XML output with indentation
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Write the XML content to the specified file
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);

            log.info("XML file successfully created: {}", xmlFilePath);
        } catch (Exception e) {
            log.error("An error occurred while exporting data to XML: ", e);
        }
    }
}
