package de.zeus.hermes.as400;

import com.ibm.as400.access.AS400;
import de.zeus.hermes.util.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Manages connections to AS400 systems.
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Creating and managing AS400 connections</li>
 *     <li>Decrypting credentials when provided in encrypted form</li>
 *     <li>Providing methods to connect and disconnect from the AS400 system</li>
 * </ul>
 * </p>
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
@Slf4j
@Component
public class System400Manager {

    private AS400 as400;

    // Encryption key for decrypting credentials.
    // This key should be managed securely in a real application.
    private final String encryptionKey = "IhrVerschlüsselungsschlüssel";

    /**
     * Obtains an AS400 connection.
     * <p>
     * If no connection exists, a new one is created using the provided system address and encrypted credentials.
     * </p>
     *
     * @param system            the AS400 system address.
     * @param encryptedUsername the encrypted username.
     * @param encryptedPassword the encrypted password.
     * @return the AS400 connection instance.
     */
    public AS400 getAs400(String system, String encryptedUsername, String encryptedPassword) {
        if (as400 == null) {
            try {
                String username = EncryptionUtil.decrypt(encryptionKey, encryptedUsername);
                String password = EncryptionUtil.decrypt(encryptionKey, encryptedPassword);
                as400 = new AS400(system, username, password);
                log.info("AS400 connection successfully established.");
            } catch (Exception e) {
                log.error("Error establishing AS400 connection.", e);
            }
        }
        return as400;
    }

    /**
     * Disconnects the AS400 connection and releases its resources.
     */
    public void disconnect() {
        if (as400 != null) {
            as400.disconnectAllServices();
            as400 = null;
            log.info("AS400 connection closed.");
        }
    }
}
