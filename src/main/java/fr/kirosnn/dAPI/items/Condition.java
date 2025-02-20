package fr.kirosnn.dAPI.items;

import org.bukkit.entity.Player;

/**
 * The interface Condition.
 */
public interface Condition {
    /**
     * Test boolean.
     *
     * @param player the player
     * @return the boolean
     */
    boolean test(Player player);
}