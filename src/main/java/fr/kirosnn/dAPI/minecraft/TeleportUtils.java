package fr.kirosnn.dAPI.minecraft;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for teleporting players with effects, sounds, cooldowns, and messages.
 */
public class TeleportUtils {

    /**
     * Teleports a player with optional effects, cooldown, and messages.
     *
     * @param player          The target player
     * @param location        The destination location
     * @param cooldown        Time in seconds before teleporting (0 for instant)
     * @param preTeleportMsg  Message before teleportation (null to disable)
     * @param postTeleportMsg Message after teleportation (null to disable)
     * @param soundEffect     Sound to play after teleporting (null to disable)
     * @param particleEffect  Particle effect to display before teleporting (null to disable)
     */
    public static void teleport(Player player, Location location, int cooldown,
                                String preTeleportMsg, String postTeleportMsg,
                                Sound soundEffect, Particle particleEffect) {
        if (player == null || location == null) return;

        if (preTeleportMsg != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', preTeleportMsg));
        }

        if (particleEffect != null) {
            player.spawnParticle(particleEffect, player.getLocation(), 30, 0.5, 1, 0.5);
        }

        if (cooldown > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    performTeleport(player, location, postTeleportMsg, soundEffect);
                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("YourPluginName"), cooldown * 20L);
        } else {
            performTeleport(player, location, postTeleportMsg, soundEffect);
        }
    }

    /**
     * Performs the actual teleportation with optional message and sound.
     */
    private static void performTeleport(@NotNull Player player, Location location, String postTeleportMsg, Sound soundEffect) {
        player.teleport(location);

        if (postTeleportMsg != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', postTeleportMsg));
        }

        if (soundEffect != null) {
            player.playSound(player.getLocation(), soundEffect, 1.0f, 1.0f);
        }
    }
}
