package fr.kirosnn.dAPI.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class YamlFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration configuration;

    /**
     * Constructeur du gestionnaire YAML en lecture seule
     *
     * @param plugin   Plugin Bukkit/Spigot
     * @param fileName Nom du fichier YAML (ex. "config.yml")
     */
    public YamlFile(@NotNull JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Récupère une valeur dans le fichier YAML.
     *
     * @param path         Chemin de la clé
     * @param defaultValue Valeur par défaut si la clé n'existe pas
     * @return Valeur associée à la clé ou null si absente
     */
    public <T> T get(String path, T defaultValue) {
        if (configuration.contains(path)) {
            return (T) configuration.get(path);
        }
        return defaultValue;
    }

    /**
     * Recharge le fichier YAML depuis le disque.
     */
    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Récupère la configuration brute.
     *
     * @return FileConfiguration associée
     */
    public FileConfiguration getConfig() {
        return configuration;
    }
}