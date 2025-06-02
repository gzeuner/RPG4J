package de.zeus.hermes.camel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.as400.access.AS400;
import de.zeus.hermes.as400.QueueManager;
import de.zeus.hermes.config.DynamicPropertyResolver;
import de.zeus.hermes.entity.RequestEntity;
import de.zeus.hermes.service.RequestResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataQueueRoute extends RouteBuilder {

    private final DynamicPropertyResolver resolver;
    private final QueueManager queueManager;
    private final RequestResponseService requestResponseService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String resolve(String key) {
        return resolver.resolve(null, key);
    }

    @Override
    public void configure() {
        log.info("\uD83D\uDD0C Configuring DataQueue Camel Route for RPG → Java via DB2");

        // Konfiguration aus application.yaml via DynamicPropertyResolver
        String system = resolve("as400.system");
        String username = resolve("as400.username");
        String password = resolve("as400.password");
        String library = resolve("dataqueue.library");
        String fromQueue = resolve("dataqueue.java2rpg");
        int maxEntryLength = Integer.parseInt(resolve("dataqueue.max-entry-length"));

        log.info("\uD83D\uDCF1 IBM i Verbindung zu '{}' mit User '{}'", system, maskUsername(username));

        AS400 as400 = new AS400(system, username, password.toCharArray());
        queueManager.getJavaToRpg(library, fromQueue, as400, maxEntryLength);

        String fromUri = String.format("jt400://%s:%s@%s/QSYS.LIB/%s.LIB/%s.DTAQ?keyed=false&format=binary",
                username, password, system, library, fromQueue);

        from(fromUri)
                .routeId("rpgFileCommandRoute")
                .log("\uD83D\uDCE5 [RPG → Java] Received DataQueue entry")
                .log("📨 Request payload: ${body}")
                .process(exchange -> {
                    String body = exchange.getIn().getBody(String.class);
                    log.info("Empfangenes JSON: {}", body);

                    try {
                        JsonNode json = objectMapper.readTree(body);
                        String clientId = json.path("clientId").asText("default");
                        String command = json.path("command").asText();
                        JsonNode argsNode = json.path("args");

                        if (command.isBlank()) {
                            log.warn("⚠Kein gültiger Befehl in Payload: {}", body);
                            return;
                        }

                        String argsJson = objectMapper.writeValueAsString(argsNode);
                        log.info("Aufruf createRequest mit clientId={}, command={}, argsJson={}", clientId, command, argsJson);

                        RequestEntity request = requestResponseService.createRequest(clientId, command, argsJson);

                        log.info("Request persisted: {}", request.getId());

                    } catch (Exception e) {
                        log.error("Fehler beim Parsen oder Speichern des Requests", e);
                    }
                });

    }

    private String maskUsername(String user) {
        if (user == null || user.length() <= 2) return "**";
        return user.charAt(0) + "*".repeat(user.length() - 2) + user.charAt(user.length() - 1);
    }
}
