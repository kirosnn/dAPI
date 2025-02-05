package fr.kirosnn.dAPI.tab;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The type Tab api.
 */
public class TabAPI {

    private final JavaPlugin plugin;
    private final Map<UUID, PlayerTab> playerTabs = new HashMap<>();
    private int updateTaskId = -1;

    /**
     * Instantiates a new Tab api.
     *
     * @param plugin the plugin
     */
    public TabAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets tab.
     *
     * @param player the player
     * @param header the header
     * @param footer the footer
     */
    public void setTab(@NotNull Player player, String header, String footer) {
        if (!player.isOnline()) return;

        UUID playerId = player.getUniqueId();
        PlayerTab tab = playerTabs.computeIfAbsent(playerId, id -> new PlayerTab(player));

        tab.setHeader(header);
        tab.setFooter(footer);
        tab.update();
    }

    /**
     * Remove tab.
     *
     * @param player the player
     */
    public void removeTab(@NotNull Player player) {
        PlayerTab tab = playerTabs.remove(player.getUniqueId());
        if (tab != null) {
            tab.clear();
        }
    }

    /**
     * Clear all tabs.
     */
    public void clearAllTabs() {
        playerTabs.values().forEach(PlayerTab::clear);
        playerTabs.clear();
    }

    /**
     * Start auto update.
     *
     * @param interval the interval
     * @param header   the header
     * @param footer   the footer
     */
    public void startAutoUpdate(int interval, String header, String footer) {
        stopAutoUpdate();

        updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setTab(player, header, footer);
            }
        }, 0L, interval);
    }

    /**
     * Stop auto update.
     */
    public void stopAutoUpdate() {
        if (updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
        }
    }
}