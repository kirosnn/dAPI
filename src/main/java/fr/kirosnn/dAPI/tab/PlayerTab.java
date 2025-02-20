package fr.kirosnn.dAPI.tab;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

/**
 * The type Player tab.
 */
public class PlayerTab {

    private final Player player;
    private String header;
    private String footer;

    /**
     * Instantiates a new Player tab.
     *
     * @param player the player
     */
    public PlayerTab(@NotNull Player player) {
        this.player = Objects.requireNonNull(player, "Le joueur ne peut pas être null !");
        this.header = SimpleTextParser.parse((header != null ? header : "").replace("%player_name%", player.getName()));
        this.footer = SimpleTextParser.parse((footer != null ? footer : "").replace("%player_name%", player.getName()));
    }

    /**
     * Sets header.
     *
     * @param header the header
     */
    public void setHeader(@NotNull String header) {
        this.header = SimpleTextParser.parse((header != null ? header : "").replace("%player_name%", player.getName()));
    }

    /**
     * Sets footer.
     *
     * @param footer the footer
     */
    public void setFooter(@NotNull String footer) {
        this.footer = SimpleTextParser.parse((footer != null ? footer : "").replace("%player_name%", player.getName()));
    }

    /**
     * Update.
     */
    public void update() {
        try {
            Object packet = getTabPacket(header, footer);
            sendPacket(player, packet);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Erreur lors de la mise à jour du Tab pour " + player.getName(), e);
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        setHeader("");
        setFooter("");
        update();
    }

    private @NotNull Object getTabPacket(String header, String footer) throws Exception {
        Class<?> iChatBaseComponent = getNMSClass("net.minecraft.network.chat.IChatBaseComponent");
        Class<?> chatSerializer = getNMSClass("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
        Class<?> packetClass = getNMSClass("net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter");

        // Utilisation du ChatSerializer pour créer des composants de texte
        Method aMethod = chatSerializer.getMethod("a", String.class);
        Object headerText = aMethod.invoke(null, "{\"text\":\"" + header + "\"}");
        Object footerText = aMethod.invoke(null, "{\"text\":\"" + footer + "\"}");

        // Construction du packet
        return packetClass.getConstructor(iChatBaseComponent, iChatBaseComponent).newInstance(headerText, footerText);
    }

    private void sendPacket(@NotNull Player player, Object packet) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);

        Field connectionField = Arrays.stream(handle.getClass().getDeclaredFields())
                .filter(field -> field.getType().getSimpleName().contains("Connection"))
                .findFirst()
                .orElseThrow(() -> new NoSuchFieldException("Impossible de trouver le champ de connexion !"));

        connectionField.setAccessible(true);
        Object connection = connectionField.get(handle);

        Method sendPacket = connection.getClass().getMethod("a", getNMSClass("net.minecraft.network.protocol.Packet"));
        sendPacket.invoke(connection, packet);
    }

    private @NotNull Class<?> getNMSClass(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Impossible de trouver la classe NMS : " + name, e);
            throw e;
        }
    }
}