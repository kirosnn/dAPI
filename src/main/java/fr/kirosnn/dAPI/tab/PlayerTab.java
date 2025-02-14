package fr.kirosnn.dAPI.tab;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

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
    public PlayerTab(Player player) {
        this.player = player;
        this.header = "";
        this.footer = "";
    }

    /**
     * Sets header.
     *
     * @param header the header
     */
    public void setHeader(@NotNull String header) {
        this.header = SimpleTextParser.parse(header.replace("%player_name%", player.getName()));
    }

    /**
     * Sets footer.
     *
     * @param footer the footer
     */
    public void setFooter(@NotNull String footer) {
        this.footer = SimpleTextParser.parse(footer.replace("%player_name%", player.getName()));
    }

    /**
     * Update.
     */
    public void update() {
        try {
            Object packet = getTabPacket(header, footer);
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
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
        Class<?> chatComponentText = getNMSClass("ChatComponentText");
        Class<?> packetPlayOutPlayerListHeaderFooter = getNMSClass("PacketPlayOutPlayerListHeaderFooter");

        Object headerText = chatComponentText.getConstructor(String.class).newInstance(header);
        Object footerText = chatComponentText.getConstructor(String.class).newInstance(footer);

        Object packet = packetPlayOutPlayerListHeaderFooter.getConstructor().newInstance();
        packetPlayOutPlayerListHeaderFooter.getField("a").set(packet, headerText);
        packetPlayOutPlayerListHeaderFooter.getField("b").set(packet, footerText);

        return packet;
    }

    private void sendPacket(@NotNull Player player, Object packet) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        Method sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
        sendPacket.invoke(playerConnection, packet);
    }

    private @NotNull Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getServerVersion() + "." + name);
    }

    private @NotNull String getServerVersion() {
        String[] versionParts = Bukkit.getServer().getBukkitVersion().split("\\.");

        if (versionParts.length < 3) {
            return "unknown";
        }

        String major = versionParts[0];
        String minor = versionParts[1];
        String patch = versionParts.length >= 3 ? versionParts[2].split("-")[0] : "0";

        return major + "." + minor + "." + patch;
    }
}