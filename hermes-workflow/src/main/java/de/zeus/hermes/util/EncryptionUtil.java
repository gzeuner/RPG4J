package de.zeus.hermes.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

/*
 * Copyright 2024 gzeuner (https://tiny-tool.de)
 *
 * Licensed under the Apache License, Version 2.0
 * See LICENSE file or visit: http://www.apache.org/licenses/LICENSE-2.0.
 */

/**
 * Utility class for AES encryption and decryption using CBC mode with PKCS5 padding.
 * <p>
 * Provides methods to encrypt and decrypt strings. The encrypted output is encoded in Base64.
 * The interactive CLI demonstrates the encryption and decryption operations.
 * Note: The {@code encrypt} method returns a raw Base64 encoded string; callers may wrap it
 * with "ENC(" and ")" if desired.
 * </p>
 */
@Slf4j
public final class EncryptionUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String ENCRYPTION_PREFIX = "ENC(";
    private static final String ENCRYPTION_SUFFIX = ")";
    private static SecretKey cachedSecretKey;

    // Private constructor to prevent instantiation.
    private EncryptionUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Generates or retrieves a cached AES {@link SecretKey} derived from the provided key string.
     * The key is hashed using SHA-256 and truncated to 16 bytes for AES-128 compatibility.
     *
     * @param key the raw key string from which to derive the secret key.
     * @return the derived {@link SecretKey}.
     * @throws GeneralSecurityException if key generation fails.
     */
    private static SecretKey getSecretKey(final String key) throws GeneralSecurityException {
        if (cachedSecretKey == null) {
            // Hash the key using SHA-256 to ensure consistent length.
            final MessageDigest sha = MessageDigest.getInstance("SHA-256");
            final byte[] keyBytes = sha.digest(key.getBytes(StandardCharsets.UTF_8));
            // Truncate the hash to 16 bytes for AES-128 compatibility.
            cachedSecretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 16), KEY_ALGORITHM);
        }
        return cachedSecretKey;
    }

    /**
     * Encrypts a string using AES with a randomly generated IV.
     * <p>
     * The returned value is a raw Base64 encoded string. It is recommended that callers wrap the output
     * with "ENC(" and ")" markers if they intend to use {@link #isEncrypted(String)} and {@link #extractEncrypted(String)}.
     * </p>
     *
     * @param key   the encryption key.
     * @param value the plaintext value to encrypt.
     * @return the Base64 encoded encrypted string, or null if encryption fails.
     */
    public static String encrypt(final String key, final String value) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            final byte[] ivBytes = new byte[16];
            new SecureRandom().nextBytes(ivBytes); // Generate a random IV.
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key), iv);

            final byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            final byte[] combined = new byte[ivBytes.length + encrypted.length];
            System.arraycopy(ivBytes, 0, combined, 0, ivBytes.length);
            System.arraycopy(encrypted, 0, combined, ivBytes.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (GeneralSecurityException e) {
            log.error("Encryption failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Decrypts a Base64-encoded string encrypted with AES, extracting the IV from the first 16 bytes.
     * <p>
     * If the input string is not wrapped in the expected "ENC(...)" markers, it is returned unchanged.
     * </p>
     *
     * @param key            the decryption key.
     * @param encryptedValue the encrypted string, optionally in "ENC(&lt;base64&gt;)" format.
     * @return the decrypted plaintext string, or null if decryption fails.
     */
    public static String decrypt(final String key, final String encryptedValue) {
        try {
            if (!isEncrypted(encryptedValue)) {
                return encryptedValue; // Return unchanged if not in the expected encrypted format.
            }
            final byte[] combined = Base64.getDecoder().decode(extractEncrypted(encryptedValue));
            final byte[] ivBytes = Arrays.copyOfRange(combined, 0, 16);
            final byte[] encryptedBytes = Arrays.copyOfRange(combined, 16, combined.length);

            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            final IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key), iv);

            final byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            log.error("Decryption failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Checks if a string is in the encrypted format "ENC(&lt;base64&gt;)".
     *
     * @param value the string to check.
     * @return true if the string matches the encrypted format, false otherwise.
     */
    public static boolean isEncrypted(final String value) {
        return value != null && value.matches("^" + ENCRYPTION_PREFIX + ".+" + ENCRYPTION_SUFFIX + "$");
    }

    /**
     * Extracts the Base64-encoded content from an encrypted string by removing the "ENC(" and ")" markers.
     *
     * @param value the encrypted string.
     * @return the Base64-encoded content if the string is encrypted; otherwise, returns the original string.
     */
    public static String extractEncrypted(final String value) {
        return isEncrypted(value)
                ? value.substring(ENCRYPTION_PREFIX.length(), value.length() - ENCRYPTION_SUFFIX.length())
                : value;
    }

    /**
     * Interactive CLI for testing encryption and decryption operations.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(final String[] args) {
        try (final Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== EncryptionUtil ===");
            System.out.print("Enter encryption key: ");
            final String key = scanner.nextLine().trim();
            try {
                getSecretKey(key);
            } catch (GeneralSecurityException e) {
                System.err.println("Invalid key. Exiting.");
                return;
            }

            while (true) {
                System.out.println("\nChoose an operation:");
                System.out.println("1. Encrypt");
                System.out.println("2. Decrypt");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                final String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter text to encrypt: ");
                        final String textToEncrypt = scanner.nextLine().trim();
                        final String encrypted = encrypt(key, textToEncrypt);
                        System.out.println("Encrypted: " + (encrypted != null ? "ENC(" + encrypted + ")" : "null"));
                    }
                    case "2" -> {
                        System.out.print("Enter text to decrypt: ");
                        final String textToDecrypt = scanner.nextLine().trim();
                        final String decrypted = decrypt(key, textToDecrypt);
                        System.out.println("Decrypted: " + (decrypted != null ? decrypted : "null"));
                    }
                    case "3" -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.err.println("Invalid choice. Try again.");
                }
            }
        }
    }
}
