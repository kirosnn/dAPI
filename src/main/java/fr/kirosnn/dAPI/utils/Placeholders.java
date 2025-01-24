package fr.kirosnn.dAPI.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing placeholders.
 */
public class Placeholders {
    private static final Map<String, Object> placeholders = new HashMap<>();

    /**
     * Adds or updates a placeholder.
     *
     * @param key   The placeholder key (e.g., "%server_name%")
     * @param value The associated value (e.g., "MyAwesomeServer")
     */
    public static void setPlaceholder(String key, Object value) {
        placeholders.put(key, value);
    }

    /**
     * Retrieves the value of a placeholder.
     *
     * @param key The placeholder key (e.g., "%server_name%")
     * @return The associated value, or null if the placeholder does not exist
     */
    public static Object getPlaceholder(String key) {
        return placeholders.get(key);
    }

    /**
     * Checks if a placeholder exists.
     *
     * @param key The placeholder key
     * @return true if the placeholder exists, otherwise false
     */
    public static boolean hasPlaceholder(String key) {
        return placeholders.containsKey(key);
    }

    /**
     * Removes a placeholder.
     *
     * @param key The placeholder key
     */
    public static void removePlaceholder(String key) {
        placeholders.remove(key);
    }
}
