package fr.kirosnn.dAPI.utils.text.simpletext;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Color utils.
 */
public class ColorUtils {

    /**
     * The constant COLOR_MAP.
     */
    public static final Map<String, String> COLOR_MAP;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("red", "§c");
        map.put("green", "§a");
        map.put("blue", "§9");
        map.put("yellow", "§e");
        map.put("gold", "§6");
        map.put("aqua", "§b");
        map.put("white", "§f");
        map.put("black", "§0");
        map.put("gray", "§7");
        map.put("dark_gray", "§8");
        map.put("dark_red", "§4");
        map.put("dark_green", "§2");
        map.put("dark_blue", "§1");
        map.put("dark_aqua", "§3");
        map.put("dark_purple", "§5");
        map.put("light_purple", "§d");
        map.put("dark_yellow", "§6");
        COLOR_MAP = Collections.unmodifiableMap(map);
    }

    /**
     * Convert hex to bukkit string.
     *
     * @param hex the hex
     * @return the string
     */
    public static @NotNull String convertHexToBukkit(@NotNull String hex) {
        StringBuilder result = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            result.append("§").append(c);
        }
        return result.toString();
    }
}