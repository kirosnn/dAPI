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

/**
 * YAML file handler utility class.
 */
public class YamlFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration configuration;

    /**
     * Constructor for the YAML file manager.
     *
     * @param plugin     Bukkit/Spigot plugin
     * @param folderPath Relative folder path inside the plugin directory (null or empty for the root folder)
     * @param fileName   YAML file name (e.g., "config.yml")
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
     * Retrieves a value from the YAML file.
     *
     * @param <T>          the type parameter
     * @param path         Key path
     * @param defaultValue Default value if the key does not exist
     * @return Value associated with the key or the default value
     */
    public <T> T get(String path, T defaultValue) {
        if (configuration.contains(path)) {
            return (T) configuration.get(path);
        }
        return defaultValue;
    }

    /**
     * Retrieves a value from the YAML file, translates color codes (§ to &)
     * and replaces placeholders.
     *
     * @param path         Key path
     * @param defaultValue Default value if the key does not exist
     * @param placeholders Map of placeholders to replace in the value
     * @return Translated value with placeholders replaced or the default value
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
     * Retrieves a list of elements from the YAML file.
     *
     * @param <T>  Type of elements in the list
     * @param path Key path
     * @return List of elements or null if absent
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path) {
        return (List<T>) configuration.getList(path);
    }

    /**
     * Retrieves a list of elements from the YAML file with a default value.
     *
     * @param <T>          the type parameter
     * @param path         Key path
     * @param defaultValue Default value if the key does not exist
     * @return List of elements or the default value
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, List<T> defaultValue) {
        List<T> list = (List<T>) configuration.getList(path);
        return (list != null) ? list : defaultValue;
    }

    /**
     * Retrieves a set of child keys for a given path.
     *
     * @param path     Key path
     * @param deepMode If true, includes subkeys recursively
     * @return Set of child keys
     */
    public Set<String> getKeys(String path, boolean deepMode) {
        return configuration.getConfigurationSection(path) != null
                ? configuration.getConfigurationSection(path).getKeys(deepMode)
                : Collections.emptySet();
    }

    /**
     * Sets a value in the YAML file.
     *
     * @param path  Key path
     * @param value Value to set
     */
    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    /**
     * Reloads the YAML file from disk.
     */
    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the current configuration to the file.
     */
    public void save() {
        try {
            configuration.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save the file " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves the raw configuration.
     *
     * @return Associated FileConfiguration
     */
    public FileConfiguration getConfig() {
        return configuration;
    }

    /**
     * Checks if a key exists in the YAML file.
     *
     * @param path Key path
     * @return True if the key exists, otherwise False
     */
    public boolean contains(String path) {
        return configuration.contains(path);
    }

    /**
     * Check if a file exists.
     *
     * @return boolean
     */
    public boolean exists() {
        return file.exists() && configuration != null;
    }

    /**
     * Retrieves a configuration section for a given path.
     *
     * @param path Path to the section
     * @return Associated ConfigurationSection or null if nonexistent
     */
    public ConfigurationSection getConfigurationSection(String path) {
        return configuration.getConfigurationSection(path);
    }
}