package fr.kirosnn.dAPI.minecraft;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * The type Broadcast utils.
 */
public class BroadcastUtils {

    /**
     * Sends a global chat message.
     *
     * @param message The message to send.
     */
    public static void sendGlobalMessage(String message) {
        if (message == null || message.isEmpty()) return;

        String formattedMessage = SimpleTextParser.parse(message);
        Bukkit.broadcastMessage(formattedMessage);
    }

    /**
     * Sends an action bar message to all online players.
     *
     * @param message The message to send.
     */
    public static void sendActionBar(String message) {
        if (message == null || message.isEmpty()) return;

        String formattedMessage = SimpleTextParser.parse(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
        }
    }

    /**
     * Sends a title to all online players.
     *
     * @param title    The title.
     * @param subtitle The subtitle.
     * @param fadeIn   The fade-in time.
     * @param stay     The stay duration.
     * @param fadeOut  The fade-out time.
     */
    public static void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        String formattedTitle = SimpleTextParser.parse(title);
        String formattedSubtitle = SimpleTextParser.parse(subtitle);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Plays a global sound for all online players.
     *
     * @param sound  The sound to play.
     * @param volume The volume.
     * @param pitch  The pitch.
     */
    public static void sendGlobalSound(Sound sound, float volume, float pitch) {
        if (sound == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Sends a global announcement with a message and optional sound.
     *
     * @param message The announcement message.
     * @param sound   The sound to play (can be null).
     */
    public static void sendGlobalAnnouncement(String message, Sound sound) {
        sendGlobalMessage(message);
        if (sound != null) {
            sendGlobalSound(sound, 1.0f, 1.0f);
        }
    }
}