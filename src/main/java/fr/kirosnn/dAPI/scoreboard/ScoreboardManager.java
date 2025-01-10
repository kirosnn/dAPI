package fr.kirosnn.dAPI.scoreboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    private final Map<Player, PlayerScoreboard> playerScoreboards = new HashMap<>();

    /**
     * Crée ou met à jour un scoreboard pour un joueur.
     *
     * @param player Joueur cible
     */
    public void setScoreboard(Player player, String title) {
        PlayerScoreboard scoreboard = playerScoreboards.computeIfAbsent(player, PlayerScoreboard::new);
        scoreboard.setTitle(title);
        scoreboard.update();
    }

    /**
     * Supprime le scoreboard d’un joueur.
     *
     * @param player Joueur cible
     */
    public void removeScoreboard(Player player) {
        if (playerScoreboards.containsKey(player)) {
            playerScoreboards.get(player).clear();
            playerScoreboards.remove(player);
        }
    }

    /**
     * Supprime tous les scoreboards.
     */
    public void clearAll() {
        for (PlayerScoreboard scoreboard : playerScoreboards.values()) {
            scoreboard.clear();
        }
        playerScoreboards.clear();
    }
}