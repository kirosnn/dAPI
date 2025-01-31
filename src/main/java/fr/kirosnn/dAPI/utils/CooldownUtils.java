package fr.kirosnn.dAPI.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The type Cooldown utils.
 */
public class CooldownUtils {

    private static final Map<Player, Map<String, Long>> playerCooldowns = new HashMap<>();

    /**
     * Has cooldown boolean.
     *
     * @param player the player
     * @param action the action
     * @return the boolean
     */
    public static boolean hasCooldown(Player player, String action) {
        Map<String, Long> cooldowns = playerCooldowns.get(player);
        if (cooldowns == null || !cooldowns.containsKey(action)) {
            return false;
        }

        long cooldownTime = cooldowns.get(action);
        long currentTime = System.currentTimeMillis();

        return currentTime < cooldownTime;
    }

    /**
     * Gets remaining time.
     *
     * @param player the player
     * @param action the action
     * @return the remaining time
     */
    public static long getRemainingTime(Player player, String action) {
        if (!hasCooldown(player, action)) return 0;

        Map<String, Long> cooldowns = playerCooldowns.get(player);
        long cooldownTime = cooldowns.get(action);
        long currentTime = System.currentTimeMillis();

        return TimeUnit.MILLISECONDS.toSeconds(cooldownTime - currentTime);
    }

    /**
     * Sets cooldown.
     *
     * @param player  the player
     * @param action  the action
     * @param seconds the seconds
     */
    public static void setCooldown(Player player, String action, long seconds) {
        playerCooldowns.putIfAbsent(player, new HashMap<>());
        Map<String, Long> cooldowns = playerCooldowns.get(player);

        long cooldownEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        cooldowns.put(action, cooldownEnd);
    }

    /**
     * Clear cooldown.
     *
     * @param player the player
     * @param action the action
     */
    public static void clearCooldown(Player player, String action) {
        if (playerCooldowns.containsKey(player)) {
            playerCooldowns.get(player).remove(action);
        }
    }

    /**
     * Clear all cooldowns.
     *
     * @param player the player
     */
    public static void clearAllCooldowns(Player player) {
        playerCooldowns.remove(player);
    }
}