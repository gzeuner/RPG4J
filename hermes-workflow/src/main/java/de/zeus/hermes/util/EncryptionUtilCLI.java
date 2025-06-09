package de.zeus.hermes.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import java.util.Scanner;

/**
 * CLI-Tool zum Ver- und Entschlüsseln von Strings mit Jasypt (kompatibel zu Spring Boot).
 *
 * Verwendung:
 * Starte die Main-Methode und folge den Eingabeaufforderungen.
 */
public class EncryptionUtilCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Jasypt Encryption Tool (Spring Boot kompatibel)");
        System.out.print("Encryption password: ");
        String password = scanner.nextLine();

        PooledPBEStringEncryptor encryptor = createEncryptor(password);

        while (true) {
            System.out.print("\n(E)ncrypt, (D)ecrypt oder (Q)uit? ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "q":
                    System.out.println("Bye!");
                    return;
                case "e":
                    System.out.print("Klartext eingeben: ");
                    String plainText = scanner.nextLine();
                    String encrypted = encryptor.encrypt(plainText);
                    System.out.println("Verschlüsselt: ENC(" + encrypted + ")");
                    break;
                case "d":
                    System.out.print("Verschlüsselten Text eingeben (mit oder ohne ENC(...)): ");
                    String encryptedInput = scanner.nextLine();
                    String base64 = stripEncWrapper(encryptedInput);
                    try {
                        String decrypted = encryptor.decrypt(base64);
                        System.out.println("Entschlüsselt: " + decrypted);
                    } catch (Exception ex) {
                        System.err.println("Entschlüsselung fehlgeschlagen: " + ex.getMessage());
                    }
                    break;
                default:
                    System.out.println("Bitte (E), (D) oder (Q) eingeben.");
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
