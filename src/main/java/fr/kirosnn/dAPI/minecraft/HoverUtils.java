package fr.kirosnn.dAPI.minecraft;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The type Hover utils.
 */
public class HoverUtils {

    /**
     * Sends a hover message to a player.
     *
     * @param player       The player receiving the message.
     * @param message      The full message.
     * @param hoverPart    The part of the message that will have a hover effect.
     * @param hoverMessage The hover text that appears when hovering over {@code hoverPart}.
     */
    public static void sendHoverMessage(Player player, String message, String hoverPart, String hoverMessage) {
        if (player == null || message == null || hoverPart == null || hoverMessage == null) return;

        BaseComponent[] finalMessage = buildHoverText(message, hoverPart, hoverMessage, null, null);
        player.spigot().sendMessage(finalMessage);
    }

    /**
     * Sends a hoverable and clickable message to a player.
     *
     * @param player       The player receiving the message.
     * @param message      The full message.
     * @param hoverPart    The part of the message that will have a hover effect.
     * @param hoverMessage The hover text that appears when hovering over {@code hoverPart}.
     * @param clickAction  The click action (e.g., OPEN_URL, RUN_COMMAND).
     * @param clickValue   The value associated with the click action.
     */
    public static void sendHoverClickableMessage(Player player, String message, String hoverPart, String hoverMessage, ClickEvent.Action clickAction, String clickValue) {
        if (player == null || message == null || hoverPart == null || hoverMessage == null || clickAction == null || clickValue == null)
            return;

        BaseComponent[] finalMessage = buildHoverText(message, hoverPart, hoverMessage, clickAction, clickValue);
        player.spigot().sendMessage(finalMessage);
    }

    /**
     * Builds a message with a hover and optional click event.
     *
     * @param message      The full message.
     * @param hoverPart    The part of the message that will have hover effects.
     * @param hoverMessage The hover text.
     * @param clickAction  The click event type (optional).
     * @param clickValue   The value for the click event (optional).
     * @return An array of {@link BaseComponent} containing the formatted message.
     */
    @Contract("_, _, _, _, _ -> new")
    private static BaseComponent @NotNull [] buildHoverText(String message, String hoverPart, String hoverMessage, ClickEvent.Action clickAction, String clickValue) {
        String formattedMessage = SimpleTextParser.parse(message);
        String formattedHover = SimpleTextParser.parse(hoverMessage);

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