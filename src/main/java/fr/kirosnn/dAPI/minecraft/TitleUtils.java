package fr.kirosnn.dAPI.minecraft;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for sending titles to players.
 */
public class TitleUtils {

    /**
     * Sends a title with default fade durations.
     *
     * @param player   The player to receive the title.
     * @param title    The title text.
     * @param subtitle The subtitle text.
     */
    public static void sendTitle(@NotNull Player player, String title, String subtitle) {
        sendTitle(player, title, subtitle, 10, 70, 20);
    }

    /**
     * Sends a title with customizable fade durations.
     *
     * @param player   The player to receive the title.
     * @param title    The title text.
     * @param subtitle The subtitle text.
     * @param fadeIn   The fade-in duration (in ticks).
     * @param stay     The duration the title stays (in ticks).
     * @param fadeOut  The fade-out duration (in ticks).
     */
    public static void sendTitle(@NotNull Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        String formattedTitle = (title != null) ? SimpleTextParser.parse(title) : "";
        String formattedSubtitle = (subtitle != null) ? SimpleTextParser.parse(subtitle) : "";

        player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, stay, fadeOut);
    }
}