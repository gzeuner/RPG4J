package de.zeus.hermes.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;

    @Value("${jasypt.encryptor.key-obtention-iterations}")
    private String iterations;

    @Value("${jasypt.encryptor.pool-size}")
    private String poolSize;

    @Value("${jasypt.encryptor.provider-name}")
    private String provider;

    @Value("${jasypt.encryptor.salt-generator-classname}")
    private String saltGenerator;

    @Value("${jasypt.encryptor.string-output-type}")
    private String outputType;

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(algorithm);
        config.setKeyObtentionIterations(iterations);
        config.setPoolSize(poolSize);
        config.setProviderName(provider);
        config.setSaltGeneratorClassName(saltGenerator);
        config.setStringOutputType(outputType);

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }
}
