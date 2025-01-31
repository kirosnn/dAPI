package fr.kirosnn.dAPI.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.kirosnn.dAPI.utils.text.JsonFile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class Schematic {

    private final List<BlockData> blockDataList = new ArrayList<>();
    private final Location origin;

    private Schematic(List<BlockData> blockDataList, Location origin) {
        this.blockDataList.addAll(blockDataList);
        this.origin = origin;
    }

    /**
     * Create a schematic synchronously
     */
    @Contract("_, _ -> new")
    public static @NotNull Schematic create(@NotNull Location pos1, @NotNull Location pos2) {
        List<BlockData> blockDataList = new ArrayList<>();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        Location origin = new Location(pos1.getWorld(), minX, minY, minZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = Objects.requireNonNull(pos1.getWorld()).getBlockAt(x, y, z);
                    if (!block.isEmpty()) {
                        blockDataList.add(new BlockData(block));
                    }
                }
            }
        }

        return new Schematic(blockDataList, origin);
    }

    /**
     * Save the schematic to a JSON file
     */
    public boolean save(@NotNull JavaPlugin plugin, String fileName) {
        File folder = new File(plugin.getDataFolder(), "schematics");
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().severe("Impossible de créer le dossier des schématiques.");
            return false;
        }

        File file = new File(folder, fileName + ".json");
        JsonObject schematicJson = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        Set<Material> materialSet = new HashSet<>();

        for (BlockData blockData : blockDataList) {
            materialSet.add(blockData.getMaterial());
        }

        List<Material> materialList = new ArrayList<>(materialSet);
        JsonArray materialArray = new JsonArray();

        for (Material material : materialList) {
            materialArray.add(material.name());
        }

        for (BlockData blockData : blockDataList) {
            int materialIndex = materialList.indexOf(blockData.getMaterial());
            jsonArray.add(blockData.toJson(materialIndex));
        }

        JsonObject originJson = new JsonObject();
        originJson.addProperty("x", origin.getBlockX());
        originJson.addProperty("y", origin.getBlockY());
        originJson.addProperty("z", origin.getBlockZ());
        originJson.addProperty("world", origin.getWorld().getName());

        schematicJson.add("materials", materialArray);
        schematicJson.add("blocks", jsonArray);
        schematicJson.add("origin", originJson);

        JsonFile jsonFile = new JsonFile(plugin, "schematics", fileName + ".json");
        jsonFile.setJsonObject(schematicJson);
        jsonFile.save();
        return true;
    }

    /**
     * Load a schematic synchronously
     */
    public static @NotNull Schematic load(JavaPlugin plugin, String fileName) {
        JsonFile jsonFile = new JsonFile(plugin, "schematics", fileName + ".json");
        JsonObject schematicJson = jsonFile.getJsonObject();

        JsonArray materialsArray = schematicJson.getAsJsonArray("materials");
        JsonArray blocksArray = schematicJson.getAsJsonArray("blocks");
        JsonObject originJson = schematicJson.getAsJsonObject("origin");

        World world = Bukkit.getWorld(originJson.get("world").getAsString()); // ✅ Correction
        if (world == null) throw new IllegalStateException("Le monde spécifié n'existe pas !");

        Material[] materials = new Material[materialsArray.size()];
        for (int i = 0; i < materialsArray.size(); i++) {
            materials[i] = Material.valueOf(materialsArray.get(i).getAsString());
        }

        List<BlockData> blockDataList = new ArrayList<>();
        for (int i = 0; i < blocksArray.size(); i++) {
            blockDataList.add(BlockData.fromJson(blocksArray.get(i).getAsJsonObject(), materials));
        }

        Location origin = new Location(world, originJson.get("x").getAsInt(), originJson.get("y").getAsInt(), originJson.get("z").getAsInt());

        return new Schematic(blockDataList, origin);
    }

    /**
     * Paste blocks from the schematic asynchronously to avoid server lag
     */
    public void paste(@NotNull Location targetLocation, boolean applyPhysics) {
        World world = targetLocation.getWorld();
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (int i = 0; i < blockDataList.size(); i++) {
                final BlockData blockData = blockDataList.get(i);
                final int delay = i / 100;

                Bukkit.getScheduler().runTaskLater(plugin, () -> blockData.paste(targetLocation, applyPhysics), delay);
            }
        });
    }
}