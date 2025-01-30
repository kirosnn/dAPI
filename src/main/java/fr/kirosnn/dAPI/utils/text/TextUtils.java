package fr.kirosnn.dAPI.utils.text;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for text-related operations.
 */
public class TextUtils {

    /**
     * Converts color codes from '&' to '§'.
     *
     * @param text The input string with '&' as color code.
     * @return The formatted string with '§' as color code.
     */
    public static @NotNull String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}