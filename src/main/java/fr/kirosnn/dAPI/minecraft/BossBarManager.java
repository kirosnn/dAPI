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
 * Handles animated BossBars for players.
 */
public class BossBarManager {

    private static final Map<Player, BossBar> bossBars = new HashMap<>();

    /**
     * Creates and shows a boss bar for a player.
     *
     * @param player   The player.
     * @param message  The message.
     * @param color    The bar color.
     * @param style    The bar style.
     * @param progress The initial progress (0.0 - 1.0).
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
     * Updates the boss bar message and progress.
     *
     * @param player   The player.
     * @param message  The new message.
     * @param progress The new progress (0.0 - 1.0).
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
     * Removes the boss bar from a player.
     *
     * @param player The player.
     */
    public static void removeBossBar(Player player) {
        if (player == null) return;

        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }
}