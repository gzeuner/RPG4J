package de.zeus.hermes.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for transforming XML files using XSLT.
 */
public class XMLtransformer {

    private static final Logger LOGGER = Logger.getLogger(XMLtransformer.class.getName());

    /**
     * Transforms an XML file using an XSLT file and outputs the result to a JSON file.
     *
     * @param xmlFileName The name (path) of the XML file to transform.
     * @param xsltFileName The name (path) of the XSLT file to use for the transformation.
     * @param jsonFileName The name (path) where the transformation result will be saved.
     */
    public static void transformXML(String xmlFileName, String xsltFileName, String jsonFileName) {
        try {
            Source xsltSource = loadXSLTSource(xsltFileName);

            // Load the XML file to be transformed
            File xmlFile = new File(xmlFileName);
            Source xmlSource = new StreamSource(xmlFile);

            // Configure the transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            // Set the target for the transformation result
            try (OutputStream os = new FileOutputStream(jsonFileName)) {
                Result result = new StreamResult(os);

                // Perform the transformation
                transformer.transform(xmlSource, result);
                LOGGER.log(Level.INFO, "Transformation completed successfully. File: {0}", jsonFileName);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during transformation.", e);
        }
    }

    /**
     * Loads the XSLT source, first attempting to find the file in the file system, then in the jar.
     *
     * @param xsltFileName The name (path) of the XSLT file.
     * @return The Source object representing the XSLT file.
     */
    private static Source loadXSLTSource(String xsltFileName) {
        File xsltFile = new File(xsltFileName);
        if (!xsltFile.exists()) {
            LOGGER.log(Level.SEVERE, "XSLT file not found in the current directory: {0}", xsltFileName);
            throw new RuntimeException("XSLT file not found in the current directory: " + xsltFileName);
        }
        LOGGER.log(Level.INFO, "Loading XSLT file from the current directory: {0}", xsltFileName);
        return new StreamSource(xsltFile);
    }

}
