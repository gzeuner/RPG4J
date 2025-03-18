package de.zeus.hermes.as400;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.DataQueue;
import com.ibm.as400.access.QSYSObjectPathName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Manager for handling DataQueues between Java and RPG.
 * <p>
 * This class provides methods to obtain the DataQueue for Java-to-RPG and RPG-to-Java communications.
 * The queues are lazily initialized upon first access.
 * </p>
 */
@Slf4j
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
    public DataQueue getJavaToRpg(String library, String queueName, AS400 as400) {
        if (javaToRpg == null) {
            String path = QSYSObjectPathName.toPath(library, queueName, "DTAQ");
            javaToRpg = new DataQueue(as400, path);
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
    public DataQueue getRpgToJava(String library, String queueName, AS400 as400) {
        if (rpgToJava == null) {
            String path = QSYSObjectPathName.toPath(library, queueName, "DTAQ");
            rpgToJava = new DataQueue(as400, path);
            log.info("RPG-to-Java DataQueue initialized: {}", path);
        }
        return rpgToJava;
    }
}
