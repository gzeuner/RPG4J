package de.zeus.hermes.as400;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Manages DataQueues for communication between Java and RPG applications.
 * <p>
 * This class provides functionality to:
 * <ul>
 *     <li>Access DataQueues for Java-to-RPG communication</li>
 *     <li>Access DataQueues for RPG-to-Java communication</li>
 *     <li>Lazily initialize queues on first access</li>
 * </ul>
 * </p>
 *
 * @author gzeuner
 * @version 1.0
 * @since 2024
 */
@Slf4j
@Getter
@Component
public class QueueManager {

    private DataQueue javaToRpg;
    private DataQueue rpgToJava;

    /**
     * Returns the Java-to-RPG DataQueue, initializing it if it hasn't been created yet.
     *
     * @param library   the library where the queue is located.
     * @param queueName the name of the queue.
     * @param as400     the AS400 connection.
     * @return the DataQueue for Java-to-RPG messages.
     */
    public DataQueue getJavaToRpg(String library, String queueName, AS400 as400, int entryLength) {
        if (javaToRpg == null) {
            String path = QSYSObjectPathName.toPath(library, queueName, "DTAQ");
            javaToRpg = new DataQueue(as400, path);
            ensureExists(javaToRpg, library, queueName, entryLength);
            log.info("Java-to-RPG DataQueue initialized: {}", path);
        }
        return javaToRpg;
    }


    /**
     * Returns the RPG-to-Java DataQueue, initializing it if it hasn't been created yet.
     *
     * @param library   the library where the queue is located.
     * @param queueName the name of the queue.
     * @param as400     the AS400 connection.
     * @return the DataQueue for RPG-to-Java messages.
     */
    public DataQueue getRpgToJava(String library, String queueName, AS400 as400, int entryLength) {
        if (rpgToJava == null) {
            String path = QSYSObjectPathName.toPath(library, queueName, "DTAQ");
            rpgToJava = new DataQueue(as400, path);
            ensureExists(rpgToJava, library, queueName, entryLength);
            log.info("RPG-to-Java DataQueue initialized: {}", path);
        }
        return rpgToJava;
    }

    private void ensureExists(DataQueue queue, String library, String name, int entryLength) {
        try {
            if (!queue.exists()) {
                log.info("Creating DataQueue '{}' in library '{}' with entryLength={}", name, library, entryLength);
                queue.create(entryLength);
            }
        } catch (Exception e) {
            log.error("Failed to create DataQueue '{}': {}", name, e.getMessage(), e);
        }
    }
}
