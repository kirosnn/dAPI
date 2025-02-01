package fr.kirosnn.dAPI.utils.text;

import fr.kirosnn.dAPI.utils.text.simpletext.SimpleTextParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * The type Yaml file.
 */
public class YamlFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration configuration;

    /**
     * Instantiates a new Yaml file.
     *
     * @param plugin     the plugin
     * @param folderPath the folder path
     * @param fileName   the file name
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
     * Get t.
     *
     * @param <T>          the type parameter
     * @param path         the path
     * @param defaultValue the default value
     * @return the t
     */
    public <T> T get(String path, T defaultValue) {
        if (configuration.contains(path)) {
            return (T) configuration.get(path);
        }
        return defaultValue;
    }

    /**
     * Gets translated.
     *
     * @param path         the path
     * @param defaultValue the default value
     * @param placeholders the placeholders
     * @return the translated
     */
    public String getTranslated(String path, String defaultValue, Map<String, String> placeholders) {
        String value = get(path, defaultValue);

        if (value == null) {
            return defaultValue;
        }

        if (placeholders == null) {
            placeholders = Collections.emptyMap();
        }

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = "\\{" + entry.getKey() + "\\}";
            value = value.replaceAll(key, entry.getValue());
        }

        return SimpleTextParser.parse(value);
    }

    /**
     * Gets list.
     *
     * @param <T>  the type parameter
     * @param path the path
     * @return the list
     */
    public <T> List<T> getList(String path) {
        return (List<T>) configuration.getList(path);
    }

    /**
     * Gets list.
     *
     * @param <T>          the type parameter
     * @param path         the path
     * @param defaultValue the default value
     * @return the list
     */
    public <T> List<T> getList(String path, List<T> defaultValue) {
        List<T> list = (List<T>) configuration.getList(path);
        return (list != null) ? list : defaultValue;
    }

    /**
     * Gets keys.
     *
     * @param path     the path
     * @param deepMode the deep mode
     * @return the keys
     */
    public Set<String> getKeys(String path, boolean deepMode) {
        return configuration.getConfigurationSection(path) != null
                ? Objects.requireNonNull(configuration.getConfigurationSection(path)).getKeys(deepMode)
                : Collections.emptySet();
    }

    /**
     * Set.
     *
     * @param path  the path
     * @param value the value
     */
    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    /**
     * Reload.
     */
    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Save.
     */
    public void save() {
        try {
            configuration.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save the file " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public FileConfiguration getConfig() {
        return configuration;
    }

    /**
     * Contains boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public boolean contains(String path) {
        return configuration.contains(path);
    }

    /**
     * Exists boolean.
     *
     * @return the boolean
     */
    public boolean exists() {
        return file.exists() && configuration != null;
    }

    /**
     * Gets configuration section.
     *
     * @param path the path
     * @return the configuration section
     */
    public ConfigurationSection getConfigurationSection(String path) {
        return configuration.getConfigurationSection(path);
    }
}