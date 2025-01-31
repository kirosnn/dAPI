package fr.kirosnn.dAPI.minecraft;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * The type Broadcast utils.
 */
public class BroadcastUtils {

    /**
     * Send global message.
     *
     * @param message the message
     */
    public static void sendGlobalMessage(String message) {
        if (message == null || message.isEmpty()) return;

        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.broadcastMessage(formattedMessage);
    }

    /**
     * Send action bar.
     *
     * @param message the message
     */
    public static void sendActionBar(String message) {
        if (message == null || message.isEmpty()) return;

        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
        }
    }

    /**
     * Send title.
     *
     * @param title    the title
     * @param subtitle the subtitle
     * @param fadeIn   the fade in
     * @param stay     the stay
     * @param fadeOut  the fade out
     */
    public static void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        String formattedTitle = ChatColor.translateAlternateColorCodes('&', title);
        String formattedSubtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Send global sound.
     *
     * @param sound  the sound
     * @param volume the volume
     * @param pitch  the pitch
     */
    public static void sendGlobalSound(Sound sound, float volume, float pitch) {
        if (sound == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Send global announcement.
     *
     * @param message the message
     * @param sound   the sound
     */
    public static void sendGlobalAnnouncement(String message, Sound sound) {
        sendGlobalMessage(message);
        if (sound != null) {
            sendGlobalSound(sound, 1.0f, 1.0f);
        }
    }
}