package fr.kirosnn.dAPI.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Placeholders.
 */
public class Placeholders {
    private static final Map<String, Object> placeholders = new HashMap<>();

    /**
     * Sets placeholder.
     *
     * @param key   the key
     * @param value the value
     */
    public static void setPlaceholder(String key, Object value) {
        placeholders.put(key, value);
    }

    /**
     * Gets placeholder.
     *
     * @param key the key
     * @return the placeholder
     */
    public static Object getPlaceholder(String key) {
        return placeholders.get(key);
    }

    /**
     * Has placeholder boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public static boolean hasPlaceholder(String key) {
        return placeholders.containsKey(key);
    }

    /**
     * Remove placeholder.
     *
     * @param key the key
     */
    public static void removePlaceholder(String key) {
        placeholders.remove(key);
    }
}
