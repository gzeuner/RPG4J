package de.zeus.hermes.adapter;
/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Interface for abstracting queue operations.
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
public interface QueueConnectionPoint {

    /**
     * Reads a message from the queue.
     *
     * @return the message read from the queue as a String.
     */
    String readMessage();

    /**
     * Writes a message to the queue.
     *
     * @param message the message to be written to the queue.
     */
    void writeMessage(String message);
}
