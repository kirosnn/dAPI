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

public class ScoreboardAPI {
    private final Map<UUID, PlayerScoreboard> playerScoreboards = new HashMap<>();
    private final JavaPlugin plugin;
    private int updateTaskId = -1;

    public ScoreboardAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

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

    public void removeScoreboard(@NotNull Player player) {
        PlayerScoreboard scoreboard = playerScoreboards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.clear();
        }
    }

    public void clearAllScoreboards() {
        playerScoreboards.values().forEach(PlayerScoreboard::clear);
        playerScoreboards.clear();
    }

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

        public PlayerScoreboard(@NotNull Player player) {
            player.setScoreboard(this.scoreboard);
        }

        private Objective createObjective() {
            Objective obj = this.scoreboard.registerNewObjective("main", "dummy", SimpleTextParser.parse("Scoreboard"));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            return obj;
        }

        public void setTitle(String title) {
            if (this.objective != null) {
                this.objective.setDisplayName(SimpleTextParser.parse(title));
            }
        }

        public void setLines(List<String> lines) {
            this.lines.clear();
            this.lines.addAll(lines);
        }

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

        public boolean isValid() {
            return this.objective != null && this.scoreboard.getObjective("main") != null;
        }

        public void clear() {
            this.scoreboard.getEntries().forEach(this.scoreboard::resetScores);
        }
    }

    public static class ScoreboardData {
        private final String title;
        private final List<String> lines;

        public ScoreboardData(String title, List<String> lines) {
            this.title = title;
            this.lines = lines;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getLines() {
            return lines;
        }
    }
}