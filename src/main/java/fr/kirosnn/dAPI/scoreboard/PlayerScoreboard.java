package fr.kirosnn.dAPI.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerScoreboard {

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> lines;

    public PlayerScoreboard(@NotNull Player player) {
        this.player = player;
        this.scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("main", "dummy", ChatColor.RESET + "");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.lines = new ArrayList<>();
        player.setScoreboard(scoreboard);
    }

    /**
     * Définit le titre du scoreboard.
     *
     * @param title Titre du scoreboard
     */
    public void setTitle(String title) {
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
    }

    /**
     * Met à jour les lignes du scoreboard.
     */
    public void update() {
        clear();
        for (int i = 0; i < lines.size(); i++) {
            String line = ChatColor.translateAlternateColorCodes('&', lines.get(i));
            objective.getScore(line).setScore(lines.size() - i);
        }
    }

    /**
     * Définit les lignes du scoreboard.
     *
     * @param lines Liste des lignes
     */
    public void setLines(List<String> lines) {
        this.lines.clear();
        this.lines.addAll(lines);
    }

    /**
     * Ajoute une ligne au scoreboard.
     *
     * @param line Ligne à ajouter
     */
    public void addLine(String line) {
        lines.add(line);
    }

    /**
     * Efface les lignes du scoreboard.
     */
    public void clear() {
        scoreboard.getEntries().forEach(scoreboard::resetScores);
    }
}