package fr.kirosnn.dAPI.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardUpdater {

    private final ScoreboardManager scoreboardManager;
    private final JavaPlugin plugin;
    private int taskId;

    public ScoreboardUpdater(JavaPlugin plugin, ScoreboardManager scoreboardManager) {
        this.plugin = plugin;
        this.scoreboardManager = scoreboardManager;
    }

    /**
     * Démarre la mise à jour automatique des scoreboards.
     *
     * @param interval Intervalle en ticks
     */
    public void start(int interval) {
        stop();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::updateAll, 0L, interval);
    }

    /**
     * Arrête la mise à jour automatique.
     */
    public void stop() {
        if (taskId != 0) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    /**
     * Met à jour tous les scoreboards.
     */
    private void updateAll() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            scoreboardManager.setScoreboard(player, "Scoreboard Title"); 
        });
    }
}