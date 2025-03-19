package de.zeus.hermes.config;

import de.zeus.hermes.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Objects;

/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0
 */

/**
 * Configuration class for setting up the DataSource.
 * <p>
 * This class creates a DataSource bean using database properties from the environment.
 * Credentials are decrypted if they are provided in encrypted format.
 * </p>
 *
 * @author gzeuner
 * @version 1.0.1
 * @since 2024
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    private final Environment env;

    /**
     * Creates and configures a {@link DataSource} bean.
     *
     * @return the configured {@link DataSource}
     */
    @Bean
    public DataSource dataSource() {
        try {
            // Retrieve the encryption key from the environment
            String encryptionKey = env.getProperty("encryption.key", "defaultSecretKey1234");

            // Retrieve database properties from the environment
            String driverClassName = env.getProperty("spring.datasource.driver-class-name");
            String url = env.getProperty("spring.datasource.url");
            String username = decryptIfNeeded(env.getProperty("spring.datasource.username"), encryptionKey);
            String password = decryptIfNeeded(env.getProperty("spring.datasource.password"), encryptionKey);

            log.info("Initializing database connection: {}", url);

            // Build and return the DataSource
            return DataSourceBuilder.create()
                    .driverClassName(driverClassName)
                    .url(url)
                    .username(username)
                    .password(password)
                    .build();

        } catch (Exception e) {
            log.error("Error configuring datasource: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid datasource configuration", e);
        }
    }

    /**
     * Decrypts the given value if it is in an encrypted format.
     * <p>
     * If the value is wrapped in the format {@code ENC(...)} then it is decrypted using the provided encryption key.
     * </p>
     *
     * @param value         the value to be potentially decrypted.
     * @param encryptionKey the encryption key used for decryption.
     * @return the decrypted value if applicable; otherwise, the original value.
     * @throws NullPointerException if decryption fails (resulting in a null value).
     */
    private String decryptIfNeeded(String value, String encryptionKey) {
        // Return value if null, empty, or not encrypted
        if (value == null || value.isEmpty() || value.equals("ENC()")) {
            return value;
        }
        // Decrypt value if it is in encrypted format
        if (EncryptionUtil.isEncrypted(value)) {
            String decryptedValue = EncryptionUtil.decrypt(encryptionKey, EncryptionUtil.extractEncrypted(value));
            return Objects.requireNonNull(decryptedValue, "Decryption failed");
        }
        return value;
    }
}
