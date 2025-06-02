package de.zeus.hermes.as400;

import com.ibm.as400.access.AS400;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Manages connections to AS400 systems using Jasypt for credential decryption.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Creating and managing AS400 connections</li>
 *     <li>Decrypting credentials using Jasypt if marked as ENC(...)</li>
 *     <li>Providing methods to connect and disconnect from the AS400 system</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class System400Manager {

    private AS400 as400;

    @Value("${jasypt.encryptor.password}")
    private String encryptionPassword;

    /**
     * Obtains an AS400 connection.
     * If no connection exists, a new one is created using the provided system address and encrypted credentials.
     *
     * @param system            the AS400 system address.
     * @param encryptedUsername the encrypted username (possibly ENC(...))
     * @param encryptedPassword the encrypted password (possibly ENC(...))
     * @return the AS400 connection instance.
     */
    public synchronized AS400 getAs400(String system, String encryptedUsername, String encryptedPassword) {
        if (as400 == null) {
            try {
                String username = decryptIfNeeded(encryptedUsername);
                String password = decryptIfNeeded(encryptedPassword);
                as400 = new AS400(system, username, password);
                log.info("✅ AS400 connection successfully established.");
            } catch (Exception e) {
                log.error("❌ Error establishing AS400 connection.", e);
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

    /**
     * Decrypts a value using Jasypt if it's in ENC(...) format.
     *
     * @param value the possibly encrypted string
     * @return decrypted or plain string
     */
    private String decryptIfNeeded(String value) {
        if (value != null && value.startsWith("ENC(") && value.endsWith(")")) {
            String encryptedPart = value.substring(4, value.length() - 1);
            BasicTextEncryptor decryptor = new BasicTextEncryptor();
            decryptor.setPassword(encryptionPassword);
            return decryptor.decrypt(encryptedPart);
        }
        return value;
    }
}
