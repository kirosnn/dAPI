package fr.kirosnn.dAPI.minecraft;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for teleportation with effects and messages.
 */
public class TeleportUtils {

    /**
     * Teleports a player to a specified location with optional cooldown, messages, and effects.
     *
     * @param player          The player to teleport.
     * @param location        The destination location.
     * @param cooldown        The cooldown in seconds before teleportation occurs.
     * @param preTeleportMsg  The message sent before teleportation.
     * @param postTeleportMsg The message sent after teleportation.
     * @param soundEffect     The sound effect played after teleportation.
     * @param particleEffect  The particle effect displayed before teleportation.
     * @param plugin          The plugin instance used for scheduling tasks.
     */
    public static void teleport(Player player, Location location, int cooldown,
                                String preTeleportMsg, String postTeleportMsg,
                                Sound soundEffect, Particle particleEffect,
                                JavaPlugin plugin) {
        if (player == null || location == null || plugin == null) return;

        if (preTeleportMsg != null && !preTeleportMsg.isEmpty()) {
            player.sendMessage(SimpleTextParser.parse(preTeleportMsg));
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

    /**
     * Executes the actual teleportation, playing effects and sending messages.
     *
     * @param player          The player being teleported.
     * @param location        The destination location.
     * @param postTeleportMsg The message sent after teleportation.
     * @param soundEffect     The sound effect played after teleportation.
     */
    private static void performTeleport(@NotNull Player player, Location location, String postTeleportMsg, Sound soundEffect) {
        player.teleport(location);

        if (postTeleportMsg != null && !postTeleportMsg.isEmpty()) {
            player.sendMessage(SimpleTextParser.parse(postTeleportMsg));
        }

        if (soundEffect != null) {
            player.playSound(player.getLocation(), soundEffect, 1.0f, 1.0f);
        }
    }
}