package fr.kirosnn.dAPI.minecraft;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Boss bar manager.
 */
public class BossBarManager {

    private static final Map<Player, BossBar> bossBars = new HashMap<>();

    /**
     * Show boss bar.
     *
     * @param player   the player
     * @param message  the message
     * @param color    the color
     * @param style    the style
     * @param progress the progress
     */
    public static void showBossBar(Player player, String message, BarColor color, BarStyle style, double progress) {
        if (player == null || message == null) return;

        String formattedMessage = SimpleTextParser.parse(message);
        BossBar bossBar = Bukkit.createBossBar(formattedMessage, color, style);
        bossBar.setProgress(progress);
        bossBar.addPlayer(player);

        bossBars.put(player, bossBar);
    }

    /**
     * Update boss bar.
     *
     * @param player   the player
     * @param message  the message
     * @param progress the progress
     */
    public static void updateBossBar(Player player, String message, double progress) {
        if (player == null || message == null) return;

        BossBar bossBar = bossBars.get(player);
        if (bossBar != null) {
            bossBar.setTitle(SimpleTextParser.parse(message));
            bossBar.setProgress(progress);
        }
    }

    /**
     * Remove boss bar.
     *
     * @param player the player
     */
    public static void removeBossBar(Player player) {
        if (player == null) return;

        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }
}