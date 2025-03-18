package de.zeus.hermes.config;

import de.zeus.hermes.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicPropertyResolver {

    private final Environment env;

    /**
     * Attempts to use the provided value. If it is null, the default value from application.yml is used.
     * If the value is encrypted (ENC(...)), it is decrypted.
     *
     * @param value           the preferred value (may be null)
     * @param defaultProperty the key for the default value in application.yml
     * @return the decrypted or default value, or null if neither is available.
     */
    public String resolve(final String value, final String defaultProperty) {
        if (value != null) {
            log.info("Value for '{}' already set: '{}'", defaultProperty, maskSensitiveValue(value));
            return decryptIfNeeded(value);
        }

        final String defaultValue = env.getProperty(defaultProperty);
        if (defaultValue != null) {
            log.info("Using fallback from application.yml for '{}': '{}'", defaultProperty, maskSensitiveValue(defaultValue));
            return decryptIfNeeded(defaultValue);
        }

        log.warn("No default value found for '{}'. Returning NULL.", defaultProperty);
        return null;
    }

    /**
     * Decrypts the given value if it is in the format ENC(...).
     *
     * @param value the value to potentially decrypt.
     * @return the decrypted value if applicable; otherwise, the original value.
     */
    private String decryptIfNeeded(final String value) {
        if (EncryptionUtil.isEncrypted(value)) {
            final String encryptionKey = env.getProperty("encryption.key", "defaultSecretKey1234");
            return EncryptionUtil.decrypt(encryptionKey, EncryptionUtil.extractEncrypted(value));
        }
        return value;
    }

    /**
     * Masks sensitive values (such as passwords) for logging purposes.
     *
     * @param value the value to mask.
     * @return "*****" if the value length is greater than 5 characters; otherwise, returns the original value.
     */
    private String maskSensitiveValue(final String value) {
        return (value != null && value.length() > 5) ? "*****" : value;
    }
}
