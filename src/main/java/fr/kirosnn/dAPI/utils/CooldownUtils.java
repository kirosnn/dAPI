package fr.kirosnn.dAPI.utils;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for managing global cooldowns.
 */
public class CooldownUtils {

    private static final Map<Player, Map<String, Long>> playerCooldowns = new HashMap<>();

    /**
     * Checks if the player has a cooldown for the given action.
     *
     * @param player The player to check
     * @param action The action (e.g., "tp", "command_name")
     * @return True if the player has a cooldown, false otherwise
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
     * Gets the remaining time for a cooldown in seconds.
     *
     * @param player The player to check
     * @param action The action (e.g., "tp", "command_name")
     * @return The remaining time in seconds
     */
    public static long getRemainingTime(Player player, String action) {
        if (!hasCooldown(player, action)) return 0;

        Map<String, Long> cooldowns = playerCooldowns.get(player);
        long cooldownTime = cooldowns.get(action);
        long currentTime = System.currentTimeMillis();

        return TimeUnit.MILLISECONDS.toSeconds(cooldownTime - currentTime);
    }

    /**
     * Sets a cooldown for a specific action for the player.
     *
     * @param player  The player to set the cooldown for
     * @param action  The action (e.g., "tp", "command_name")
     * @param seconds The cooldown time in seconds
     */
    public static void setCooldown(Player player, String action, long seconds) {
        playerCooldowns.putIfAbsent(player, new HashMap<>());
        Map<String, Long> cooldowns = playerCooldowns.get(player);

        long cooldownEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
        cooldowns.put(action, cooldownEnd);
    }

    /**
     * Clears the cooldown for a specific action for the player.
     *
     * @param player The player to remove the cooldown from
     * @param action The action to remove the cooldown for
     */
    public static void clearCooldown(Player player, String action) {
        if (playerCooldowns.containsKey(player)) {
            playerCooldowns.get(player).remove(action);
        }
    }

    /**
     * Clears all cooldowns for a specific player.
     *
     * @param player The player to clear all cooldowns for
     */
    public static void clearAllCooldowns(Player player) {
        playerCooldowns.remove(player);
    }
}