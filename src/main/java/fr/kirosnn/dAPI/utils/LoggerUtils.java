package fr.kirosnn.dAPI.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class LoggerUtils {

    private final JavaPlugin plugin;

    /**
     * Constructeur du LoggerUtils pour initialiser le plugin.
     *
     * @param plugin Plugin appelant
     */
    public LoggerUtils(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Affiche un message INFO avec un préfixe stylisé.
     *
     * @param message Contenu du message
     */
    public void info(@NotNull String message) {
        Bukkit.getServer().getConsoleSender().sendMessage("✦ " + message);
    }

    /**
     * Affiche un message INFO par le plugin avec un préfixe stylisé.
     *
     * @param message Contenu du message
     */
    public void infoPlugin(@NotNull String message) {
        plugin.getServer().getConsoleSender().sendMessage(plugin.getName() + " ✦ " + message);
    }

    /**
     * Affiche un message SEVERE (erreur) avec un préfixe stylisé.
     *
     * @param message Contenu du message
     */
    public void error(@NotNull String message) {
        plugin.getLogger().severe("✦ " + message);
    }

    /**
     * Affiche un message WARNING avec un préfixe stylisé.
     *
     * @param message Contenu du message
     */
    public void warn(@NotNull String message) {
        plugin.getLogger().warning("✦ " + message);
    }
}