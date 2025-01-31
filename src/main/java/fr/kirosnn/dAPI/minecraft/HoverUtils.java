package fr.kirosnn.dAPI.minecraft;

import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The type Hover utils.
 */
public class HoverUtils {

    /**
     * Send hover message.
     *
     * @param player       the player
     * @param message      the message
     * @param hoverPart    the hover part
     * @param hoverMessage the hover message
     */
    public static void sendHoverMessage(Player player, String message, String hoverPart, String hoverMessage) {
        if (player == null || message == null || hoverPart == null || hoverMessage == null) return;

        BaseComponent[] finalMessage = buildHoverText(message, hoverPart, hoverMessage, null, null);
        player.spigot().sendMessage(finalMessage);
    }

    /**
     * Send hover clickable message.
     *
     * @param player       the player
     * @param message      the message
     * @param hoverPart    the hover part
     * @param hoverMessage the hover message
     * @param clickAction  the click action
     * @param clickValue   the click value
     */
    public static void sendHoverClickableMessage(Player player, String message, String hoverPart, String hoverMessage, ClickEvent.Action clickAction, String clickValue) {
        if (player == null || message == null || hoverPart == null || hoverMessage == null || clickAction == null || clickValue == null)
            return;

        BaseComponent[] finalMessage = buildHoverText(message, hoverPart, hoverMessage, clickAction, clickValue);
        player.spigot().sendMessage(finalMessage);
    }

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