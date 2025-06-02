package de.zeus.hermes.util;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.Scanner;

/**
 * CLI-Tool zum Ver- und Entschlüsseln von Strings mit Jasypt.
 *
 * Verwendung:
 * Starte die Main-Methode und folge den Eingabeaufforderungen.
 */
public class EncryptionUtilCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Jasypt Encryption Tool");
        System.out.print("Encryption password: ");
        String password = scanner.nextLine();

        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);

        label:
        while (true) {
            System.out.print("\n(E)ncrypt, (D)ecrypt oder (Q)uit? ");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "q":
                    System.out.println("Bye!");
                    break label;
                case "e":
                    System.out.print("Klartext eingeben: ");
                    String plainText = scanner.nextLine();
                    String encrypted = textEncryptor.encrypt(plainText);
                    System.out.println("Verschlüsselt: ENC(" + encrypted + ")");
                    break;
                case "d":
                    System.out.print("Verschlüsselten Text eingeben (mit oder ohne ENC(...)): ");
                    String encryptedInput = scanner.nextLine();
                    String base64 = stripEncWrapper(encryptedInput);
                    try {
                        String decrypted = textEncryptor.decrypt(base64);
                        System.out.println("Entschlüsselt: " + decrypted);
                    } catch (Exception ex) {
                        System.err.println("Entschlüsselung fehlgeschlagen: " + ex.getMessage());
                    }
                    break;
                default:
                    System.out.println(" Bitte (E), (D) oder (Q) eingeben.");
                    break;
            }
        }

        scanner.close();
    }

    private static String stripEncWrapper(String input) {
        if (input != null && input.startsWith("ENC(") && input.endsWith(")")) {
            return input.substring(4, input.length() - 1);
        }
        return input;
    }
}
