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

public class XMLtransformer {

    private static final Logger LOGGER = Logger.getLogger(XMLtransformer.class.getName());

    public static void transformXML(String xmlFileName, String xsltFileName, String jsonFileName) {
        try {
            // Laden der XSLT-Datei
            File xsltFile = new File(xsltFileName);
            Source xsltSource = new StreamSource(xsltFile);

            // Laden der XML-Datei, die transformiert werden soll
            File xmlFile = new File(xmlFileName);
            Source xmlSource = new StreamSource(xmlFile);

            // Konfiguration des Transformers
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            // Ziel für das Ergebnis der Transformation setzen
            try (OutputStream os = new FileOutputStream(jsonFileName)) {
                Result result = new StreamResult(os);

                // Durchführung der Transformation
                transformer.transform(xmlSource, result);
                LOGGER.log(Level.INFO, "Die Transformation wurde erfolgreich durchgeführt. Datei: {0}", jsonFileName);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fehler bei der Transformation.", e);
        }
    }
}
