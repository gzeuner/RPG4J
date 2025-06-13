package de.zeus.hermes.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Apache Camel routes for XSLT-based transformations (csv, html, json, etc.).
 *
 * Dynamisch generiert für alle unterstützten Formate.
 */
@Component
public class XsltTransformationRoute extends RouteBuilder {

    @Value("${export.file-path}")
    private String exportPath;

    @Override
    public void configure() {
        for (String type : List.of("csv", "html", "json", "jsonl", "md")) {
            from("direct:transform-" + type)
                    .routeId("xslt-" + type + "-route")
                    .to("xslt:classpath:xslt/xml_to_" + type + ".xslt")
                    .toD("file:" + exportPath + "?fileName=${header.CamelFileName}");
        }
    }
}
