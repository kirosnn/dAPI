package fr.kirosnn.dAPI.utils.text.simpletext;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;

import java.util.Map;

public class ColorUtils {

    public static final Map<String, String> COLOR_MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("black", "§0"), new AbstractMap.SimpleEntry<>("dark_blue", "§1"),
            new AbstractMap.SimpleEntry<>("dark_green", "§2"), new AbstractMap.SimpleEntry<>("dark_aqua", "§3"),
            new AbstractMap.SimpleEntry<>("dark_red", "§4"), new AbstractMap.SimpleEntry<>("dark_purple", "§5"),
            new AbstractMap.SimpleEntry<>("gold", "§6"), new AbstractMap.SimpleEntry<>("gray", "§7"),
            new AbstractMap.SimpleEntry<>("dark_gray", "§8"), new AbstractMap.SimpleEntry<>("blue", "§9"),
            new AbstractMap.SimpleEntry<>("green", "§a"), new AbstractMap.SimpleEntry<>("aqua", "§b"),
            new AbstractMap.SimpleEntry<>("red", "§c"), new AbstractMap.SimpleEntry<>("light_purple", "§d"),
            new AbstractMap.SimpleEntry<>("yellow", "§e"), new AbstractMap.SimpleEntry<>("white", "§f"),
            new AbstractMap.SimpleEntry<>("bold", "§l"), new AbstractMap.SimpleEntry<>("italic", "§o"),
            new AbstractMap.SimpleEntry<>("underline", "§n"), new AbstractMap.SimpleEntry<>("strikethrough", "§m"),
            new AbstractMap.SimpleEntry<>("obfuscated", "§k"), new AbstractMap.SimpleEntry<>("reset", "§r")
    );

    public static @NotNull String convertHexToBukkit(@NotNull String hex) {
        StringBuilder result = new StringBuilder("§x");
        for (char c : hex.toCharArray()) {
            result.append("§").append(c);
        }
        return result.toString();
    }
}