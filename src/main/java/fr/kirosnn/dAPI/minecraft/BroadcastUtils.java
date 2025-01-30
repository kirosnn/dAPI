package fr.kirosnn.dAPI.minecraft;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Utility class for sending global broadcasts in various formats.
 */
public class BroadcastUtils {

    /**
     * Sends a global chat message to all players.
     *
     * @param message The message to broadcast
     */
    public static void sendGlobalMessage(String message) {
        if (message == null || message.isEmpty()) return;

        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        Bukkit.broadcastMessage(formattedMessage);
    }

    /**
     * Sends a global action bar message to all players.
     *
     * @param message The message to display in the action bar
     */
    public static void sendActionBar(String message) {
        if (message == null || message.isEmpty()) return;

        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
        }
    }

    /**
     * Sends a global title to all players.
     *
     * @param title    The title text
     * @param subtitle The subtitle text
     * @param fadeIn   Time in ticks for fade-in
     * @param stay     Time in ticks for display
     * @param fadeOut  Time in ticks for fade-out
     */
    public static void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        String formattedTitle = ChatColor.translateAlternateColorCodes('&', title);
        String formattedSubtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(formattedTitle, formattedSubtitle, fadeIn, stay, fadeOut);
        }
    }

    /**
     * Sends a global sound effect to all players.
     *
     * @param sound   The sound to play
     * @param volume  Volume level
     * @param pitch   Pitch level
     */
    public static void sendGlobalSound(Sound sound, float volume, float pitch) {
        if (sound == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Sends a global broadcast with a chat message and optional sound.
     *
     * @param message The chat message to send
     * @param sound   The sound effect to play (can be null)
     */
    public static void sendGlobalAnnouncement(String message, Sound sound) {
        sendGlobalMessage(message);
        if (sound != null) {
            sendGlobalSound(sound, 1.0f, 1.0f);
        }
    }
}