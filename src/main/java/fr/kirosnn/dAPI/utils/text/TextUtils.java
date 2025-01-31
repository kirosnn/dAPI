package fr.kirosnn.dAPI.utils.text;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * The type Text utils.
 */
public class TextUtils {

    /**
     * Colorize string.
     *
     * @param text the text
     * @return the string
     */
    public static @NotNull String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}