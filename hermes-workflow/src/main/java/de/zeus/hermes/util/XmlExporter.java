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
 */
@Slf4j
@Component
public class XmlExporter {

    /**
     * Exports a list of data entries to an XML file.
     *
     * @param data            the list of data entries, each represented as a map.
     * @param rootElementName the name of the root element in the XML document.
     * @param xmlFilePath     the file path where the XML should be saved.
     * @param columnTypes     a map defining the data types of the columns (optional).
     */
    public void exportListToXML(final List<Map<String, Object>> data,
                                final String rootElementName,
                                final String xmlFilePath,
                                final Map<String, String> columnTypes) {
        try {
            // Create a DocumentBuilderFactory and configure it
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();

            // Create and append the root element
            final Element root = doc.createElement(rootElementName);
            doc.appendChild(root);

            // Iterate through each data entry and create XML elements
            for (final Map<String, Object> row : data) {
                final Element rowElement = doc.createElement("row");

                for (final Map.Entry<String, Object> entry : row.entrySet()) {
                    final String key = entry.getKey();
                    final String value = entry.getValue() != null ? entry.getValue().toString() : "";
                    final Element columnElement = doc.createElement(key);
                    columnElement.appendChild(doc.createTextNode(value));

                    // If column types are provided, set the "type" attribute in uppercase
                    if (columnTypes != null && columnTypes.containsKey(key)) {
                        columnElement.setAttribute("type", columnTypes.get(key).toUpperCase());
                    }
                    rowElement.appendChild(columnElement);
                }
                root.appendChild(rowElement);
            }

            // Prepare the transformer to write the XML document to a file with indentation
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Transform the DOM document to the output file
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);

            log.info("XML file successfully created: {}", xmlFilePath);
        } catch (Exception e) {
            log.error("Error exporting data to XML: ", e);
        }
    }
}
