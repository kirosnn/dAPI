package fr.kirosnn.dAPI.utils.text.simpletext;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ColorUtils {

    public static final Map<String, String> COLOR_MAP = Map.ofEntries(
            Map.entry("black", "§0"), Map.entry("dark_blue", "§1"),
            Map.entry("dark_green", "§2"), Map.entry("dark_aqua", "§3"),
            Map.entry("dark_red", "§4"), Map.entry("dark_purple", "§5"),
            Map.entry("gold", "§6"), Map.entry("gray", "§7"),
            Map.entry("dark_gray", "§8"), Map.entry("blue", "§9"),
            Map.entry("green", "§a"), Map.entry("aqua", "§b"),
            Map.entry("red", "§c"), Map.entry("light_purple", "§d"),
            Map.entry("yellow", "§e"), Map.entry("white", "§f"),
            Map.entry("bold", "§l"), Map.entry("italic", "§o"),
            Map.entry("underline", "§n"), Map.entry("strikethrough", "§m"),
            Map.entry("obfuscated", "§k"), Map.entry("reset", "§r")
    );

    public static @NotNull String convertHexToBukkit(@NotNull String hex) {
        StringBuilder result = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            result.append("§").append(c);
        }
        return result.toString();
    }
}