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
import java.util.function.Function;
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
        if (player.isOnline()) {
            UUID playerId = player.getUniqueId();
            PlayerScoreboard scoreboard = playerScoreboards.computeIfAbsent(playerId, id -> new PlayerScoreboard(player));
            scoreboard.setTitle(title);
            scoreboard.setLines(lines.stream()
                    .map(line -> SimpleTextParser.parse(line.replace("%player_name%", player.getName())))
                    .collect(Collectors.toList()));
            if (scoreboard.isValid()) {
                scoreboard.update();
            }
        }
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
     * @param interval     the interval
     * @param dataFunction the data function
     */
// ✅ Modification pour accepter une fonction dynamique
    public void startAutoUpdate(int interval, Function<Player, ScoreboardData> dataFunction) {
        this.stopAutoUpdate();
        this.updateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ScoreboardData data = dataFunction.apply(player);
                this.setScoreboard(player, data.getTitle(), data.getLines());
            }
        }, 0L, interval);
    }

    /**
     * Stop auto update.
     */
    public void stopAutoUpdate() {
        if (this.updateTaskId != -1) {
            Bukkit.getScheduler().cancelTask(this.updateTaskId);
            this.updateTaskId = -1;
        }
    }

    private static class PlayerScoreboard {
        private final Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        private Objective objective = this.createObjective();
        private final LinkedHashSet<String> lines = new LinkedHashSet<>();

        /**
         * Instantiates a new Player scoreboard.
         *
         * @param player the player
         */
        public PlayerScoreboard(@NotNull Player player) {
            player.setScoreboard(this.scoreboard);
        }

        private Objective createObjective() {
            Objective obj = this.scoreboard.registerNewObjective("main", "dummy", SimpleTextParser.parse("Scoreboard"));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            return obj;
        }

        /**
         * Sets title.
         *
         * @param title the title
         */
        public void setTitle(String title) {
            if (this.objective != null) {
                this.objective.setDisplayName(SimpleTextParser.parse(title));
            }
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
            if (!this.isValid()) {
                this.objective = this.createObjective();
            }
            this.clear();
            int score = this.lines.size();
            for (String line : this.lines) {
                this.objective.getScore(SimpleTextParser.parse(line)).setScore(score--);
            }
        }

        /**
         * Is valid boolean.
         *
         * @return the boolean
         */
        public boolean isValid() {
            return this.objective != null && this.scoreboard.getObjective("main") != null;
        }

        /**
         * Clear.
         */
        public void clear() {
            this.scoreboard.getEntries().forEach(this.scoreboard::resetScores);
        }
    }

    /**
     * The type Scoreboard data.
     */
    public static class ScoreboardData {
        private final String title;
        private final List<String> lines;

        /**
         * Instantiates a new Scoreboard data.
         *
         * @param title the title
         * @param lines the lines
         */
        public ScoreboardData(String title, List<String> lines) {
            this.title = title;
            this.lines = lines;
        }

        /**
         * Gets title.
         *
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets lines.
         *
         * @return the lines
         */
        public List<String> getLines() {
            return lines;
        }
    }
}