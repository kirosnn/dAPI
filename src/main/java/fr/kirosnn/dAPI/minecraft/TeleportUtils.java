package fr.kirosnn.dAPI.minecraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * The type Teleport utils.
 */
public class TeleportUtils {

    /**
     * Teleport.
     *
     * @param player          the player
     * @param location        the location
     * @param cooldown        the cooldown
     * @param preTeleportMsg  the pre teleport msg
     * @param postTeleportMsg the post teleport msg
     * @param soundEffect     the sound effect
     * @param particleEffect  the particle effect
     * @param plugin          the plugin
     */
    public static void teleport(Player player, Location location, int cooldown,
                                String preTeleportMsg, String postTeleportMsg,
                                Sound soundEffect, Particle particleEffect,
                                JavaPlugin plugin) {
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
            }.runTaskLater(plugin, cooldown * 20L);
        } else {
            performTeleport(player, location, postTeleportMsg, soundEffect);
        }
    }

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