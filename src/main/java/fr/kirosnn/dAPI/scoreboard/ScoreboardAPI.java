package fr.kirosnn.dAPI.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Scoreboard api.
 */
public class ScoreboardAPI {

    private final Map<Player, PlayerScoreboard> playerScoreboards = new HashMap<>();
    private final JavaPlugin plugin;
    private int updateTaskId;

    /**
     * Instantiates a new Scoreboard api.
     *
     * @param plugin the plugin
     */
    public ScoreboardAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets scoreboard.
     *
     * @param player the player
     * @param title  the title
     * @param lines  the lines
     */
    public void setScoreboard(Player player, String title, List<String> lines) {
        PlayerScoreboard scoreboard = playerScoreboards.computeIfAbsent(player, PlayerScoreboard::new);
        scoreboard.setTitle(title);

        List<String> parsedLines = lines.stream()
                .map(line -> line.replace("%player_name%", player.getName()))
                .collect(Collectors.toList());

        scoreboard.setLines(parsedLines);
        scoreboard.update();
    }

    /**
     * Remove scoreboard.
     *
     * @param player the player
     */
    public void removeScoreboard(Player player) {
        PlayerScoreboard scoreboard = playerScoreboards.remove(player);
        if (scoreboard != null) {
            scoreboard.clear();
        }
    }

    /**
     * Clear all scoreboards.
     */
    public void clearAllScoreboards() {
        playerScoreboards.values().forEach(PlayerScoreboard::clear);
        playerScoreboards.clear();
    }

    /**
     * Start auto update.
     *
     * @param interval the interval
     * @param title    the title
     * @param lines    the lines
     */
    public void startAutoUpdate(int interval, String title, List<String> lines) {
        stopAutoUpdate(); // Stoppe toute tâche précédente
        updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setScoreboard(player, title, lines);
            }
        }, 0L, interval);
    }

    /**
     * Stop auto update.
     */
    public void stopAutoUpdate() {
        if (updateTaskId != 0) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = 0;
        }
    }

    private static class PlayerScoreboard {

        private final Player player;
        private final Scoreboard scoreboard;
        private final Objective objective;
        private final List<String> lines;

        /**
         * Instantiates a new Player scoreboard.
         *
         * @param player the player
         */
        public PlayerScoreboard(Player player) {
            this.player = player;
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.objective = scoreboard.registerNewObjective("main", "dummy", ChatColor.RESET + "");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            this.lines = new ArrayList<>();
            player.setScoreboard(scoreboard);
        }

        /**
         * Sets title.
         *
         * @param title the title
         */
        public void setTitle(String title) {
            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        }

        /**
         * Sets lines.
         *
         * @param lines the lines
         */
        public void setLines(List<String> lines) {
            this.lines.clear();
            this.lines.addAll(lines);
        }

        /**
         * Update scoreboard.
         */
        public void update() {
            clear();
            for (int i = 0; i < lines.size(); i++) {
                String line = ChatColor.translateAlternateColorCodes('&', lines.get(i));
                objective.getScore(line).setScore(lines.size() - i);
            }
        }

        /**
         * Clear scoreboard.
         */
        public void clear() {
            scoreboard.getEntries().forEach(scoreboard::resetScores);
        }
    }
}