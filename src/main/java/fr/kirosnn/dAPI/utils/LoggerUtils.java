package fr.kirosnn.dAPI.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The type Logger utils.
 */
public class LoggerUtils {

    private final JavaPlugin plugin;

    /**
     * Instantiates a new Logger utils.
     *
     * @param plugin the plugin
     */
    public LoggerUtils(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Info.
     *
     * @param message the message
     */
    public void info(@NotNull String message) {
        Bukkit.getServer().getConsoleSender().sendMessage("✦ " + message);
    }

    /**
     * Info plugin.
     *
     * @param message the message
     */
    public void infoPlugin(@NotNull String message) {
        plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "]" + " ✦ " + message);
    }

    /**
     * Error.
     *
     * @param message the message
     */
    public void error(@NotNull String message) {
        plugin.getLogger().severe("✦ " + message);
    }

    /**
     * Warn.
     *
     * @param message the message
     */
    public void warn(@NotNull String message) {
        plugin.getLogger().warning("✦ " + message);
    }
}