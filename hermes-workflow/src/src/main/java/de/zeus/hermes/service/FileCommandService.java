package de.zeus.hermes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
public class FileCommandService {

    public String delete(String[] args) {
        if (args.length < 1) return "ERROR;delete requires <path>";
        Path path = Paths.get(args[0]);
        try {
            boolean deleted = Files.deleteIfExists(path);
            log.info("DELETE '{}': {}", path, deleted ? "deleted" : "not found");
            return deleted ? "OK;deleted" : "ERROR;not found";
        } catch (Exception e) {
            log.error("DELETE '{}' failed", path, e);
            return "ERROR;" + e.getMessage();
        }
    }

    public String exists(String[] args) {
        if (args.length < 1) return "ERROR;exists requires <path>";
        Path path = Paths.get(args[0]);
        boolean exists = Files.exists(path);
        log.info("EXISTS '{}': {}", path, exists ? "exists" : "missing");
        return exists ? "OK;exists" : "OK;missing";
    }

    public String move(String[] args) {
        if (args.length < 2) return "ERROR;move requires <source>;<target>";
        Path source = Paths.get(args[0]);
        Path target = Paths.get(args[1]);
        try {
            Files.move(source, target);
            log.info("MOVE '{}'=> '{}': success", source, target);
            return "OK;moved";
        } catch (Exception e) {
            log.error("MOVE '{}' to '{}' failed", source, target, e);
            return "ERROR;" + e.getMessage();
        }
    }

    public String write(String[] args) {
        if (args.length < 2) return "ERROR;write requires <path>;<content>";
        Path path = Paths.get(args[0]);
        String content = args[1];
        try {
            Files.writeString(path, content);
            log.info("WRITE to '{}': {} bytes", path, content.length());
            return "OK;written";
        } catch (Exception e) {
            log.error("WRITE '{}' failed", path, e);
            return "ERROR;" + e.getMessage();
        }
    }
}
