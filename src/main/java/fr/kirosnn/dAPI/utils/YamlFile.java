package fr.kirosnn.dAPI.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

public class YamlFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration configuration;

    /**
     * Constructeur du gestionnaire YAML
     *
     * @param plugin       Plugin Bukkit/Spigot
     * @param folderPath   Chemin du dossier relatif dans le dossier du plugin (null ou vide pour le dossier racine)
     * @param fileName     Nom du fichier YAML (ex. "config.yml")
     */
    public YamlFile(@NotNull JavaPlugin plugin, String folderPath, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;

        File targetFolder = (folderPath == null || folderPath.isEmpty())
                ? plugin.getDataFolder()
                : new File(plugin.getDataFolder(), folderPath);

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
            plugin.getLogger().info("The folder " + targetFolder.getPath() + " has been created.");
        }

        this.file = new File(targetFolder, fileName);

        if (!file.exists()) {
            String resourcePath = (folderPath == null || folderPath.isEmpty())
                    ? fileName
                    : folderPath + "/" + fileName;

            plugin.saveResource(resourcePath, false);
            plugin.getLogger().info("The file " + fileName + " did not exist and has been created.");
        } else {
            plugin.getLogger().info("The file " + fileName + " already exists.");
        }

        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Récupère une valeur dans le fichier YAML.
     *
     * @param path         Chemin de la clé
     * @param defaultValue Valeur par défaut si la clé n'existe pas
     * @return Valeur associée à la clé ou la valeur par défaut
     */
    public <T> T get(String path, T defaultValue) {
        if (configuration.contains(path)) {
            return (T) configuration.get(path);
        }
        return defaultValue;
    }

    /**
     * Récupère une valeur dans le fichier YAML, traduit les codes couleurs (§ vers &)
     * et remplace les placeholders existants.
     *
     * @param path         Chemin de la clé
     * @param defaultValue Valeur par défaut si la clé n'existe pas
     * @param placeholders Map des placeholders à remplacer dans la valeur
     * @return Valeur traduite et placeholders remplacés ou la valeur par défaut
     */
    public String getTranslated(String path, String defaultValue, Map<String, String> placeholders) {
        String value = get(path, defaultValue);

        if (value == null) {
            return defaultValue;
        }

        value = value.replace("&", "§");

        if (placeholders == null) {
            placeholders = Collections.emptyMap();
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        return value;
    }

    /**
     * Récupère une liste d'éléments dans le fichier YAML.
     *
     * @param path Chemin de la clé
     * @param <T>  Type des éléments dans la liste
     * @return Liste des éléments ou null si absente
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path) {
        return (List<T>) configuration.getList(path);
    }

    /**
     * Récupère une liste d'éléments dans le fichier YAML avec une valeur par défaut.
     *
     * @param path         Chemin de la clé
     * @param defaultValue Valeur par défaut si la clé n'existe pas
     * @return Liste des éléments ou la valeur par défaut
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, List<T> defaultValue) {
        List<T> list = (List<T>) configuration.getList(path);
        return (list != null) ? list : defaultValue;
    }

    /**
     * Récupère un ensemble de clés enfants pour un chemin donné.
     *
     * @param path     Chemin de la clé
     * @param deepMode Si true, inclut les sous-clés en profondeur
     * @return Ensemble des clés enfants
     */
    public Set<String> getKeys(String path, boolean deepMode) {
        return configuration.getConfigurationSection(path) != null
                ? configuration.getConfigurationSection(path).getKeys(deepMode)
                : Collections.emptySet();
    }

    /**
     * Définit une valeur dans le fichier YAML.
     *
     * @param path  Chemin de la clé
     * @param value Valeur à définir
     */
    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    /**
     * Recharge le fichier YAML depuis le disque.
     */
    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Sauvegarde la configuration actuelle dans le fichier.
     */
    public void save() {
        try {
            configuration.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save the file " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Récupère la configuration brute.
     *
     * @return FileConfiguration associée
     */
    public FileConfiguration getConfig() {
        return configuration;
    }

    /**
     * Vérifie si une clé existe dans le fichier YAML.
     *
     * @param path Chemin de la clé
     * @return True si la clé existe, sinon False
     */
    public boolean contains(String path) {
        return configuration.contains(path);
    }

    /**
     * Récupère une section de configuration pour un chemin donné.
     *
     * @param path Chemin de la section
     * @return ConfigurationSection associée ou null si inexistante
     */
    public ConfigurationSection getConfigurationSection(String path) {
        return configuration.getConfigurationSection(path);
    }
}