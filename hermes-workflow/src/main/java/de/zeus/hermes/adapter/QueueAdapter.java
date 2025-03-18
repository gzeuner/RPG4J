package de.zeus.hermes.adapter;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.DataQueue;
import de.zeus.hermes.as400.QueueManager;
import de.zeus.hermes.as400.System400Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Adapter for interacting with AS400 DataQueues.
 * <p>
 * This adapter implements {@link QueueConnectionPoint} and provides methods to read from and write to queues.
 * The AS400 connection and DataQueue instances are obtained via {@link System400Manager} and {@link QueueManager}.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueueAdapter implements QueueConnectionPoint {

    private final System400Manager system400Manager;
    private final QueueManager queueManager;

    @Value("${dataqueue.library}")
    private String library;

    @Value("${dataqueue.rpg2java}")
    private String rpg2javaQueueName;

    @Value("${dataqueue.java2rpg}")
    private String java2rpgQueueName;

    @Value("${as400.system}")
    private String as400System;

    @Value("${as400.username}")
    private String as400Username;

    @Value("${as400.password}")
    private String as400Password;

    /**
     * Reads a message from the RPG-to-Java DataQueue.
     *
     * @return the message read from the queue as a String, or null if an error occurs.
     */
    @Override
    public String readMessage() {
        try {
            // Obtain AS400 connection using encrypted credentials if needed.
            AS400 as400 = system400Manager.getAs400(as400System, as400Username, as400Password);
            // Retrieve the RPG-to-Java DataQueue.
            DataQueue rpgToJava = queueManager.getRpgToJava(library, rpg2javaQueueName, as400);
            // Read message data from the queue.
            byte[] data = rpgToJava.read(-1).getData();
            String message = new String(data);
            log.info("Message read: {}", message);
            return message;
        } catch (Exception e) {
            log.error("Error reading message from the queue", e);
            return null;
        }
    }

    /**
     * Writes a message to the Java-to-RPG DataQueue.
     *
     * @param message the message to be written to the queue.
     */
    @Override
    public void writeMessage(String message) {
        try {
            // Obtain AS400 connection using encrypted credentials if needed.
            AS400 as400 = system400Manager.getAs400(as400System, as400Username, as400Password);
            // Retrieve the Java-to-RPG DataQueue.
            DataQueue javaToRpg = queueManager.getJavaToRpg(library, java2rpgQueueName, as400);
            // Write the message data to the queue.
            javaToRpg.write(message.getBytes());
            log.info("Message written: {}", message);
        } catch (Exception e) {
            log.error("Error writing message to the queue", e);
        }
    }
}
