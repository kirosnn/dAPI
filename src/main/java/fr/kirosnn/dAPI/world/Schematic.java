package fr.kirosnn.dAPI.world;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.kirosnn.dAPI.utils.text.JsonFile;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Schematic {

    private final List<BlockData> blockDataList = new ArrayList<>();

    private Schematic(List<BlockData> blockDataList) {
        this.blockDataList.addAll(blockDataList);
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

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = pos1.getWorld().getBlockAt(x, y, z);
                    if (!block.isEmpty()) {
                        blockDataList.add(new BlockData(block));
                    }
                }
            }
        }

        return new Schematic(blockDataList);
    }

    /**
     * Create a schematic asynchronously
     */
    @Contract("_, _, _ -> new")
    public static @NotNull CompletableFuture<Schematic> createAsync(Location pos1, Location pos2, Plugin plugin) {
        return CompletableFuture.supplyAsync(() -> create(pos1, pos2));
    }

    /**
     * Save the schematic asynchronously
     */
    public CompletableFuture<Void> saveAsync(String filePath, Plugin plugin) {
        return CompletableFuture.runAsync(() -> save(filePath));
    }

    /**
     * Save the schematic to a JSON file
     */
    public boolean save(String filePath) {
        JsonArray jsonArray = new JsonArray();

        for (BlockData blockData : blockDataList) {
            jsonArray.add(blockData.toJson());
        }

        JsonFile jsonFile = new JsonFile((JavaPlugin) null, "", new File(filePath).getName());
        jsonFile.set("blocks", jsonArray);
        jsonFile.save();
        return true;
    }

    /**
     * Load a schematic synchronously
     */
    public static @Nullable Schematic load(String filePath) {
        JsonFile jsonFile = new JsonFile((JavaPlugin) null, "", new File(filePath).getName());
        List<JsonObject> jsonObjects = jsonFile.getList("blocks", JsonObject.class);

        if (jsonObjects == null) return null;

        List<BlockData> blockDataList = new ArrayList<>();

        for (JsonObject jsonObject : jsonObjects) {
            blockDataList.add(BlockData.fromJson(jsonObject));
        }

        return new Schematic(blockDataList);
    }

    /**
     * Load a schematic asynchronously
     */
    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<Schematic> loadAsync(String filePath, Plugin plugin) {
        return CompletableFuture.supplyAsync(() -> load(filePath));
    }

    /**
     * Paste blocks from the schematic
     */
    public List<Block> paste(Location targetLocation, boolean applyPhysics) {
        List<Block> pastedBlocks = new ArrayList<>();

        for (BlockData blockData : blockDataList) {
            Block block = blockData.paste(targetLocation, applyPhysics);
            pastedBlocks.add(block);
        }

        return pastedBlocks;
    }
}