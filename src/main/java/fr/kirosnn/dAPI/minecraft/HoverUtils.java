package fr.kirosnn.dAPI.minecraft;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for creating advanced hover and click events in chat messages.
 */
public class HoverUtils {

    /**
     * Sends a chat message with a hover effect on a specific part of the text.
     *
     * @param player       The target player
     * @param message      The full message
     * @param hoverPart    The part of the message that will have the hover effect
     * @param hoverMessage The text displayed when hovering over hoverPart
     */
    public static void sendHoverMessage(Player player, String message, String hoverPart, String hoverMessage) {
        if (player == null || message == null || hoverPart == null || hoverMessage == null) return;

        BaseComponent[] finalMessage = buildHoverText(message, hoverPart, hoverMessage, null, null);
        player.spigot().sendMessage(finalMessage);
    }

    /**
     * Sends a chat message with a hover and click effect on a specific part of the text.
     *
     * @param player       The target player
     * @param message      The full message
     * @param hoverPart    The part of the message that will have the hover effect
     * @param hoverMessage The text displayed when hovering over hoverPart
     * @param clickAction  The type of click action (e.g., "RUN_COMMAND", "OPEN_URL", "SUGGEST_COMMAND")
     * @param clickValue   The value for the click action (e.g., a command or URL)
     */
    public static void sendHoverClickableMessage(Player player, String message, String hoverPart, String hoverMessage, ClickEvent.Action clickAction, String clickValue) {
        if (player == null || message == null || hoverPart == null || hoverMessage == null || clickAction == null || clickValue == null)
            return;

        BaseComponent[] finalMessage = buildHoverText(message, hoverPart, hoverMessage, clickAction, clickValue);
        player.spigot().sendMessage(finalMessage);
    }

    /**
     * Builds a message with a hover effect (and optional click effect) on a specific word.
     *
     * @param message      The full message
     * @param hoverPart    The part of the message that will have the hover effect
     * @param hoverMessage The hover text
     * @param clickAction  Optional click action (e.g., "RUN_COMMAND", "OPEN_URL")
     * @param clickValue   Optional click value
     * @return An array of BaseComponent for sending via Spigot's chat system
     */
    @Contract("_, _, _, _, _ -> new")
    private static BaseComponent @NotNull [] buildHoverText(String message, String hoverPart, String hoverMessage, ClickEvent.Action clickAction, String clickValue) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);
        String formattedHover = ChatColor.translateAlternateColorCodes('&', hoverMessage);

        TextComponent fullMessage = new TextComponent("");

        String[] parts = formattedMessage.split(hoverPart, 2);
        if (parts.length == 0) return new BaseComponent[]{new TextComponent(formattedMessage)};

        fullMessage.addExtra(new TextComponent(parts[0]));

        TextComponent hoverComponent = new TextComponent(hoverPart);
        hoverComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(formattedHover).create()));

        if (clickAction != null && clickValue != null) {
            hoverComponent.setClickEvent(new ClickEvent(clickAction, clickValue));
        }

        fullMessage.addExtra(hoverComponent);

        if (parts.length > 1) {
            fullMessage.addExtra(new TextComponent(parts[1]));
        }

        return new BaseComponent[]{fullMessage};
    }
}