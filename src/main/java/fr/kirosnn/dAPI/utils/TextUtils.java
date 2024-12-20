package fr.kirosnn.dAPI.utils;

import org.bukkit.ChatColor;

public class TextUtils {

    /**
     * Convertis les codes couleurs de '&' à '§'.
     *
     * @param text L'entrée avec '&' comme code couleur.
     * @return La fin formatté avec '§' comme code couleur.
     */
    public static String colorize(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
