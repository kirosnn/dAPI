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
     * Encode string.
     *
     * @param input the input
     * @return the string
     */
    public static String encode(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode string.
     *
     * @param base64 the base 64
     * @return the string
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
     * Encode with complexity string.
     *
     * @param input the input
     * @return the string
     */
    public static String encodeWithComplexity(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        String reversedInput = new StringBuilder(input).reverse().toString();
        return encode(reversedInput);
    }

    /**
     * Decode with complexity string.
     *
     * @param base64 the base 64
     * @return the string
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