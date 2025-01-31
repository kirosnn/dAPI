package fr.kirosnn.dAPI.utils.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Json file.
 */
public class JsonFile {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private JsonObject jsonData;
    private final Gson gson;

    /**
     * Instantiates a new Json file.
     *
     * @param plugin     the plugin
     * @param folderPath the folder path
     * @param fileName   the file name
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
     * Load.
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
     * Save.
     */
    public void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(jsonData, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Erreur lors de la sauvegarde de " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Get t.
     *
     * @param <T>  the type parameter
     * @param path the path
     * @param type the type
     * @return the t
     */
    public <T> T get(String path, Class<T> type) {
        JsonElement element = getElement(path);
        return (element != null) ? gson.fromJson(element, type) : null;
    }

    /**
     * Gets list.
     *
     * @param <T>  the type parameter
     * @param path the path
     * @param type the type
     * @return the list
     */
    public <T> List<T> getList(String path, Type type) {
        JsonElement element = getElement(path);
        return (element != null) ? gson.fromJson(element, type) : new ArrayList<>();
    }

    /**
     * Set.
     *
     * @param path  the path
     * @param value the value
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
     * Remove.
     *
     * @param path the path
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
     * Contains boolean.
     *
     * @param path the path
     * @return the boolean
     */
    public boolean contains(String path) {
        return getElement(path) != null;
    }

    /**
     * Gets raw data.
     *
     * @return the raw data
     */
    public JsonObject getRawData() {
        return jsonData;
    }

    /**
     * Sets json object.
     *
     * @param jsonObject the json object
     */
    public void setJsonObject(@NotNull JsonObject jsonObject) {
        this.jsonData = jsonObject;
        save();
    }

    /**
     * Gets json object.
     *
     * @return the json object
     */
    public @NotNull JsonObject getJsonObject() {
        return (jsonData != null) ? jsonData : new JsonObject();
    }

    /**
     * Exists boolean.
     *
     * @return the boolean
     */
    public boolean exists() {
        return file.exists() && jsonData != null;
    }

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
