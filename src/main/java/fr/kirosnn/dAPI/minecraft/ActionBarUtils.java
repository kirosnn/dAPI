package fr.kirosnn.dAPI.minecraft;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Utility class for sending Action Bar messages to players using packets.
 */
public class ActionBarUtils {

    /**
     * Sends an Action Bar message to a player.
     *
     * @param player  The target player
     * @param message The message to display
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
    }
}