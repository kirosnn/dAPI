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

public class ScoreboardAPI {

    private final Map<Player, PlayerScoreboard> playerScoreboards = new HashMap<>();
    private final JavaPlugin plugin;
    private int updateTaskId;

    public ScoreboardAPI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Crée ou met à jour un scoreboard pour un joueur.
     *
     * @param player Joueur cible
     * @param title  Titre du scoreboard
     * @param lines  Lignes du scoreboard (peuvent contenir des placeholders comme %player_name%)
     */
    public void setScoreboard(Player player, String title, List<String> lines) {
        PlayerScoreboard scoreboard = playerScoreboards.computeIfAbsent(player, PlayerScoreboard::new);
        scoreboard.setTitle(title);

        // Parse placeholders (par exemple %player_name%)
        List<String> parsedLines = lines.stream()
                .map(line -> line.replace("%player_name%", player.getName()))
                .collect(Collectors.toList());

        scoreboard.setLines(parsedLines);
        scoreboard.update();
    }

    /**
     * Supprime le scoreboard d’un joueur.
     *
     * @param player Joueur cible
     */
    public void removeScoreboard(Player player) {
        PlayerScoreboard scoreboard = playerScoreboards.remove(player);
        if (scoreboard != null) {
            scoreboard.clear();
        }
    }

    /**
     * Supprime tous les scoreboards.
     */
    public void clearAllScoreboards() {
        playerScoreboards.values().forEach(PlayerScoreboard::clear);
        playerScoreboards.clear();
    }

    /**
     * Démarre la mise à jour automatique des scoreboards pour tous les joueurs.
     *
     * @param interval Intervalle en ticks (1 tick = 50ms)
     * @param title    Titre du scoreboard
     * @param lines    Lignes du scoreboard
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
     * Arrête la mise à jour automatique des scoreboards.
     */
    public void stopAutoUpdate() {
        if (updateTaskId != 0) {
            Bukkit.getScheduler().cancelTask(updateTaskId);
            updateTaskId = 0;
        }
    }

    /**
     * Classe interne pour représenter le scoreboard d’un joueur.
     */
    private static class PlayerScoreboard {

        private final Player player;
        private final Scoreboard scoreboard;
        private final Objective objective;
        private final List<String> lines;

        public PlayerScoreboard(Player player) {
            this.player = player;
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
         * Définit les lignes du scoreboard.
         *
         * @param lines Liste des lignes
         */
        public void setLines(List<String> lines) {
            this.lines.clear();
            this.lines.addAll(lines);
        }

        /**
         * Met à jour les lignes du scoreboard.
         */
        public void update() {
            clear(); // Réinitialise les lignes existantes
            for (int i = 0; i < lines.size(); i++) {
                String line = ChatColor.translateAlternateColorCodes('&', lines.get(i));
                objective.getScore(line).setScore(lines.size() - i); // Ordre décroissant
            }
        }

        /**
         * Efface toutes les lignes du scoreboard.
         */
        public void clear() {
            scoreboard.getEntries().forEach(scoreboard::resetScores);
        }
    }
}