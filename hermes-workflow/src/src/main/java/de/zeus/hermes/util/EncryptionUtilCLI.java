package de.zeus.hermes.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import java.util.Scanner;

/**
 * CLI tool for encrypting and decrypting strings using Jasypt (Spring Boot compatible).
 *
 * Usage:
 * 1. Run the main method.
 * 2. Choose an action: (E)ncrypt, (D)ecrypt or (Q)uit.
 * 3. Enter your encryption password.
 * 4. For encryption: provide the plain text → result is ENC(base64).
 * 5. For decryption: paste an encrypted value (with or without ENC(...)) → result is the plain text.
 *
 * Example usage for Spring Boot:
 *   my.secret.property=ENC(yourEncryptedValueHere)
 *
 *   In your @Configuration class or bean:
 *   @Value("${my.secret.property}")
 *   private String mySecret;
 *
 * Default algorithm used:
 *   PBEWithMD5AndDES (legacy, supported by default in Spring Boot)
 *
 * Note:
 *   You can change the algorithm and settings inside createEncryptor().
 */
public class EncryptionUtilCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Jasypt Encryption Tool (Spring Boot compatible)");

        while (true) {
            System.out.print("\n(E)ncrypt, (D)ecrypt or (Q)uit? ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "q":
                    System.out.println("Goodbye!");
                    return;
                case "e":
                case "d":
                    System.out.print("Encryption password: ");
                    String password = scanner.nextLine();
                    PooledPBEStringEncryptor encryptor = createEncryptor(password);

                    if (choice.equals("e")) {
                        System.out.print("Enter plain text: ");
                        String plainText = scanner.nextLine();
                        String encrypted = encryptor.encrypt(plainText);
                        System.out.println("Encrypted: ENC(" + encrypted + ")");
                    } else {
                        System.out.print("Enter encrypted text (with or without ENC(...)): ");
                        String encryptedInput = scanner.nextLine();
                        String base64 = stripEncWrapper(encryptedInput);
                        try {
                            String decrypted = encryptor.decrypt(base64);
                            System.out.println("Decrypted: " + decrypted);
                        } catch (Exception ex) {
                            System.err.println("Decryption failed: " + ex.getMessage());
                        }
                    }
                    break;
                default:
                    System.out.println("Please enter E, D or Q.");
                    break;
            }
        }
    }

    private static String stripEncWrapper(String input) {
        if (input != null && input.startsWith("ENC(") && input.endsWith(")")) {
            return input.substring(4, input.length() - 1);
        }
        return input;
    }

    private static PooledPBEStringEncryptor createEncryptor(String password) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(config);
        return encryptor;
    }
}
