package fr.kirosnn.dAPI.encoder;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The type Base 64 encoder.
 */
public class Base64Encoder {

    /**
     * Encodes a string into Base64 format.
     *
     * @param input The string to encode.
     * @return The Base64 encoded string.
     */
    public static String encode(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a Base64 encoded string.
     *
     * @param base64 The Base64 encoded string to decode.
     * @return The decoded string.
     */
    @Contract("null -> fail")
    public static @NotNull String decode(String base64) {
        if (base64 == null) {
            throw new IllegalArgumentException("Base64 input cannot be null");
        }
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Encodes a string into Base64 format with added complexity (e.g., reversing the string before encoding).
     *
     * @param input The string to encode.
     * @return The Base64 encoded string with added complexity.
     */
    public static String encodeWithComplexity(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        String reversedInput = new StringBuilder(input).reverse().toString();
        return encode(reversedInput);
    }

    /**
     * Decodes a Base64 encoded string with added complexity (e.g., reversing the string after decoding).
     *
     * @param base64 The Base64 encoded string to decode.
     * @return The decoded string with added complexity reversed.
     */
    @Contract("null -> fail")
    public static @NotNull String decodeWithComplexity(String base64) {
        if (base64 == null) {
            throw new IllegalArgumentException("Base64 input cannot be null");
        }
        String decoded = decode(base64);
        return new StringBuilder(decoded).reverse().toString();
    }
}