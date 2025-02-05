package fr.kirosnn.dAPI.scoreboard;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Scoreboard api.
 */
public class ScoreboardAPI {

    private final Map<UUID, PlayerScoreboard> playerScoreboards = new HashMap<>();
    private final JavaPlugin plugin;
    private int updateTaskId = -1;

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
    public void setScoreboard(@NotNull Player player, String title, @NotNull List<String> lines) {
        if (!player.isOnline()) return;

        UUID playerId = player.getUniqueId();
        PlayerScoreboard scoreboard = playerScoreboards.computeIfAbsent(playerId, id -> new PlayerScoreboard(player));

        scoreboard.setTitle(title);
        scoreboard.setLines(lines.stream()
                .map(line -> SimpleTextParser.parse(line.replace("%player_name%", player.getName())))
                .collect(Collectors.toList()));
        scoreboard.update();
    }

    /**
     * Remove scoreboard.
     *
     * @param player the player
     */
    public void removeScoreboard(@NotNull Player player) {
        PlayerScoreboard scoreboard = playerScoreboards.remove(player.getUniqueId());
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
        stopAutoUpdate();

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
        if (updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = -1;
        }
    }

    private static class PlayerScoreboard {

        private final Scoreboard scoreboard;
        private final Objective objective;
        private final LinkedHashSet<String> lines;

        /**
         * Instantiates a new Player scoreboard.
         *
         * @param player the player
         */
        public PlayerScoreboard(@NotNull Player player) {
            this.scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
            this.objective = scoreboard.registerNewObjective("main", "dummy", SimpleTextParser.parse("Scoreboard"));
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            this.lines = new LinkedHashSet<>();
            player.setScoreboard(scoreboard);
        }

        /**
         * Sets title.
         *
         * @param title the title
         */
        public void setTitle(String title) {
            objective.setDisplayName(SimpleTextParser.parse(title));
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
         * Update.
         */
        public void update() {
            clear();
            int score = lines.size();
            for (String line : lines) {
                objective.getScore(SimpleTextParser.parse(line)).setScore(score--);
            }
        }

        /**
         * Clear.
         */
        public void clear() {
            scoreboard.getEntries().forEach(scoreboard::resetScores);
            objective.unregister();
        }
    }
}