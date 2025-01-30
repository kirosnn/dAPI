package fr.kirosnn.dAPI.utils.text;

import com.google.gson.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * JSON file handler utility class for real data storage.
 */
public class JsonFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private JsonObject jsonData;
    private final Gson gson;

    /**
     * Constructor for the JSON file manager.
     *
     * @param plugin     Bukkit/Spigot plugin
     * @param folderPath Relative folder path inside the plugin directory (null or empty for the root folder)
     * @param fileName   JSON file name (e.g., "data.json")
     */
    public JsonFile(@NotNull JavaPlugin plugin, String folderPath, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        File targetFolder = (folderPath == null || folderPath.isEmpty())
                ? plugin.getDataFolder()
                : new File(plugin.getDataFolder(), folderPath);

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
            plugin.getLogger().info("The folder " + targetFolder.getPath() + " has been created.");
        }

        this.file = new File(targetFolder, fileName);

        if (!file.exists()) {
            createNewFile();
        } else {
            load();
        }
    }

    /**
     * Creates a new empty JSON file.
     */
    private void createNewFile() {
        try {
            if (file.createNewFile()) {
                jsonData = new JsonObject();
                save();
                plugin.getLogger().info("The file " + fileName + " has been created.");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create the file " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Loads the JSON data from the file.
     */
    public void load() {
        try (Reader reader = new FileReader(file)) {
            jsonData = gson.fromJson(reader, JsonObject.class);
            if (jsonData == null) {
                jsonData = new JsonObject();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load the file " + fileName + ": " + e.getMessage());
            jsonData = new JsonObject();
        }
    }

    /**
     * Saves the JSON data to the file.
     */
    public void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(jsonData, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save the file " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves a value from the JSON file.
     *
     * @param <T>  the type parameter
     * @param path Key path
     * @param type Type of the value
     * @return Value associated with the key or null if absent
     */
    public <T> T get(String path, Class<T> type) {
        JsonElement element = jsonData.get(path);
        return (element != null) ? gson.fromJson(element, type) : null;
    }

    /**
     * Retrieves a list from the JSON file.
     *
     * @param <T>  Type of elements in the list
     * @param path Key path
     * @param type Type of elements in the list
     * @return List of elements or an empty list if absent
     */
    public <T> List<T> getList(String path, Type type) {
        JsonElement element = jsonData.get(path);
        return (element != null) ? gson.fromJson(element, type) : new ArrayList<>();
    }

    /**
     * Sets a value in the JSON file.
     *
     * @param path  Key path
     * @param value Value to set
     */
    public void set(String path, Object value) {
        jsonData.add(path, gson.toJsonTree(value));
    }

    /**
     * Removes a value from the JSON file.
     *
     * @param path Key path
     */
    public void remove(String path) {
        jsonData.remove(path);
    }

    /**
     * Checks if a key exists in the JSON file.
     *
     * @param path Key path
     * @return True if the key exists, otherwise False
     */
    public boolean contains(String path) {
        return jsonData.has(path);
    }

    /**
     * Retrieves the raw JSON data.
     *
     * @return JsonObject containing all data
     */
    public JsonObject getRawData() {
        return jsonData;
    }

    /**
     * Sets the entire JSON object in the file.
     *
     * @param jsonObject The new JsonObject to store
     */
    public void setJsonObject(@NotNull JsonObject jsonObject) {
        this.jsonData = jsonObject;
        save();
    }

    /**
     * Retrieves the entire JSON object stored in the file.
     *
     * @return The JsonObject containing all the data, or an empty object if the file is empty
     */
    public @NotNull JsonObject getJsonObject() {
        return (jsonData != null) ? jsonData : new JsonObject();
    }

    /**
     * Checks if the file exists.
     *
     * @return True if the file exists and is valid, otherwise False
     */
    public boolean exists() {
        return file.exists() && jsonData != null;
    }
}
