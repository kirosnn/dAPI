package fr.kirosnn.dAPI.items.conditions;

import fr.kirosnn.dAPI.items.Condition;
import org.bukkit.entity.Player;

/**
 * The type Permission condition.
 */
public class PermissionCondition implements Condition {
    private final String permission;

    /**
     * Instantiates a new Permission condition.
     *
     * @param permission the permission
     */
    public PermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean test(Player player) {
        return player.hasPermission(permission);
    }
}