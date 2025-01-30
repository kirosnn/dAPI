package fr.kirosnn.dAPI.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to send titles to players.
 */
public class TitleUtils {

    /**
     * Sends a basic title to a player.
     *
     * @param player   The target player
     * @param title    The title text
     * @param subtitle The subtitle text
     */
    public static void sendTitle(@NotNull Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }

    /**
     * Sends a title with a custom duration.
     *
     * @param player   The target player
     * @param title    The title text
     * @param subtitle The subtitle text
     * @param fadeIn   Time for the title to fade in (in ticks)
     * @param stay     Time for the title to stay on screen (in ticks)
     * @param fadeOut  Time for the title to fade out (in ticks)
     */
    public static void sendTitle(@NotNull Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }
}