package de.zeus.hermes.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Resolves configuration properties with fallback and masking,
 * relying on Jasypt for automatic decryption of ENC(...) values.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicPropertyResolver {

    private final Environment env;

    /**
     * Resolves a property by checking a given value, falling back to application.yml if necessary.
     * Jasypt handles decryption automatically if the value is in ENC(...) format.
     *
     * @param value           the preferred value (may be null)
     * @param defaultProperty the key for the fallback property in application.yml
     * @return the resolved (and possibly decrypted) value, or null if not available
     */
    public String resolve(final String value, final String defaultProperty) {
        if (value != null) {
            log.info("Value for '{}' already set: '{}'", defaultProperty, maskSensitiveValue(value));
            return value;
        }

        final String defaultValue = env.getProperty(defaultProperty);
        if (defaultValue != null) {
            log.info("Using fallback from application.yml for '{}': '{}'", defaultProperty, maskSensitiveValue(defaultValue));
            return defaultValue;
        }

        log.warn("No value found for '{}'. Returning NULL.", defaultProperty);
        return null;
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
