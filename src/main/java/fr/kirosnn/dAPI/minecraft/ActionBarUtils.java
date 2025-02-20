package fr.kirosnn.dAPI.minecraft;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * The type Action bar utils.
 */
public class ActionBarUtils {

    /**
     * Send action bar.
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null || message.isEmpty()) return;

        String formattedMessage = SimpleTextParser.parse(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
    }
}