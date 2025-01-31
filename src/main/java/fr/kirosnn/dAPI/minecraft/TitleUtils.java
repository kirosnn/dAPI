package fr.kirosnn.dAPI.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The type Title utils.
 */
public class TitleUtils {

    /**
     * Send title.
     *
     * @param player   the player
     * @param title    the title
     * @param subtitle the subtitle
     */
    public static void sendTitle(@NotNull Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }

    /**
     * Send title.
     *
     * @param player   the player
     * @param title    the title
     * @param subtitle the subtitle
     * @param fadeIn   the fade in
     * @param stay     the stay
     * @param fadeOut  the fade out
     */
    public static void sendTitle(@NotNull Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}