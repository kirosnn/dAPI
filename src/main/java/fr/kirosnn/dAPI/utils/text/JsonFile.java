package fr.kirosnn.dAPI.utils.text;

import com.google.gson.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (plugin == null) {
            throw new IllegalArgumentException("Le plugin ne peut pas être null !");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        File targetFolder = (folderPath == null || folderPath.isEmpty())
                ? plugin.getDataFolder()
                : new File(plugin.getDataFolder(), folderPath);

        if (!targetFolder.exists() && targetFolder.mkdirs()) {
            plugin.getLogger().info("Dossier créé : " + targetFolder.getPath());
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
                plugin.getLogger().info("Fichier créé : " + fileName);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Échec de la création du fichier " + fileName + ": " + e.getMessage());
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
            plugin.getLogger().severe("Erreur lors du chargement de " + fileName + ": " + e.getMessage());
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
            plugin.getLogger().severe("Erreur lors de la sauvegarde de " + fileName + ": " + e.getMessage());
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
        JsonElement element = getElement(path);
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
        JsonElement element = getElement(path);
        return (element != null) ? gson.fromJson(element, type) : new ArrayList<>();
    }

    /**
     * Sets a value in the JSON file.
     *
     * @param path  Key path
     * @param value Value to set
     */
    public void set(@NotNull String path, Object value) {
        String[] keys = path.split("\\.");
        JsonObject obj = jsonData;

        for (int i = 0; i < keys.length - 1; i++) {
            obj = obj.getAsJsonObject(keys[i]);
            if (obj == null) {
                obj = new JsonObject();
                jsonData.add(keys[i], obj);
            }
        }

        obj.add(keys[keys.length - 1], gson.toJsonTree(value));
        save();
    }

    /**
     * Removes a value from the JSON file.
     *
     * @param path Key path
     */
    public void remove(@NotNull String path) {
        String[] keys = path.split("\\.");
        JsonObject obj = jsonData;

        for (int i = 0; i < keys.length - 1; i++) {
            obj = obj.getAsJsonObject(keys[i]);
            if (obj == null) return;
        }

        obj.remove(keys[keys.length - 1]);
        save();
    }

    /**
     * Checks if a key exists in the JSON file.
     *
     * @param path Key path
     * @return True if the key exists, otherwise False
     */
    public boolean contains(String path) {
        return getElement(path) != null;
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

    /**
     * Retrieves a nested JSON element based on a dot-separated path.
     *
     * @param path The key path
     * @return JsonElement or null if not found
     */
    private @Nullable JsonElement getElement(@NotNull String path) {
        String[] keys = path.split("\\.");
        JsonObject obj = jsonData;
        JsonElement element = null;

        for (String key : keys) {
            element = obj.get(key);
            if (element == null) return null;
            if (element.isJsonObject()) obj = element.getAsJsonObject();
        }
        return element;
    }
}
