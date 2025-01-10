package fr.kirosnn.dAPI.utils;

import java.util.HashMap;
import java.util.Map;

public class Placeholders {
    private static final Map<String, Object> placeholders = new HashMap<>();

    /**
     * Ajoute ou modifie un placeholder.
     *
     * @param key   la clé du placeholder (exemple : "%server_name%")
     * @param value la valeur associée (exemple : "MyAwesomeServer")
     */
    public static void setPlaceholder(String key, Object value) {
        placeholders.put(key, value);
    }

    /**
     * Récupère la valeur d'un placeholder.
     *
     * @param key la clé du placeholder (exemple : "%server_name%")
     * @return la valeur associée ou null si le placeholder n'existe pas
     */
    public static Object getPlaceholder(String key) {
        return placeholders.get(key);
    }

    /**
     * Vérifie si un placeholder existe.
     *
     * @param key la clé du placeholder
     * @return true si le placeholder existe, sinon false
     */
    public static boolean hasPlaceholder(String key) {
        return placeholders.containsKey(key);
    }

    /**
     * Supprime un placeholder.
     *
     * @param key la clé du placeholder
     */
    public static void removePlaceholder(String key) {
        placeholders.remove(key);
    }
}