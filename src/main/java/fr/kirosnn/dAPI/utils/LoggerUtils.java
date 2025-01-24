package fr.kirosnn.dAPI.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for logging.
 */
public class LoggerUtils {

    private final JavaPlugin plugin;

    /**
     * Constructor for LoggerUtils to initialize the plugin.
     *
     * @param plugin The calling plugin
     */
    public LoggerUtils(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Logs an INFO message with a stylized prefix.
     *
     * @param message The content of the message
     */
    public void info(@NotNull String message) {
        Bukkit.getServer().getConsoleSender().sendMessage("✦ " + message);
    }

    /**
     * Logs an INFO message from the plugin with a stylized prefix.
     *
     * @param message The content of the message
     */
    public void infoPlugin(@NotNull String message) {
        plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "]" + " ✦ " + message);
    }

    /**
     * Logs a SEVERE (error) message with a stylized prefix.
     *
     * @param message The content of the message
     */
    public void error(@NotNull String message) {
        plugin.getLogger().severe("✦ " + message);
    }

    /**
     * Logs a WARNING message with a stylized prefix.
     *
     * @param message The content of the message
     */
    public void warn(@NotNull String message) {
        plugin.getLogger().warning("✦ " + message);
    }
}