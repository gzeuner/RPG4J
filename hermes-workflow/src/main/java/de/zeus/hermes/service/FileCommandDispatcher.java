package de.zeus.hermes.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileCommandDispatcher {

    private final FileCommandService fileCommandService;

    private final Map<String, Function<String[], String>> handlers = new HashMap<>();

    @PostConstruct
    public void initHandlers() {
        handlers.put("delete", args -> fileCommandService.delete(args));
        handlers.put("exists", args -> fileCommandService.exists(args));
        handlers.put("move",   args -> fileCommandService.move(args));
        handlers.put("write",  args -> fileCommandService.write(args));
    }

    /**
     * Verarbeitet einen vollständigen Input-String im Format "command;arg1;arg2;..."
     */
    public String dispatch(String input) {
        if (input == null || input.isBlank()) {
            log.warn("Received empty input");
            return "ERROR;Empty input";
        }

        String[] split = input.split(";", -1);
        String command = split[0].trim().toLowerCase();
        String[] args = Arrays.copyOfRange(split, 1, split.length);

        return dispatch(command, args);
    }

    /**
     * Führt einen Befehl mit Argumenten aus.
     */
    public String dispatch(String command, String[] args) {
        Function<String[], String> handler = handlers.get(command.toLowerCase());
        if (handler == null) {
            log.warn("Unsupported command '{}'", command);
            return "ERROR;Unsupported command: " + command;
        }

        return handler.apply(args);
    }

    /**
     * Wandelt ein Argument-JSON (z.B. arg1;arg2;...) in ein String-Array um.
     * Kann bei Bedarf später auf echte JSON-Verarbeitung umgestellt werden.
     */
    public String[] deserializeArgs(String argsJson) {
        if (argsJson == null || argsJson.isBlank()) {
            return new String[0];
        }
        return argsJson.split(";", -1);
    }

    /**
     * Wandelt ein Argument-Array in einen serialisierten String um.
     */
    public String serializeArgs(String[] args) {
        return String.join(";", args);
    }
}
