package fr.kirosnn.dAPI.world;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class BlockData {

    private final int x, y, z;
    private final Material material;

    public BlockData(@NotNull Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.material = block.getType();
    }

    public JsonObject toJson(int materialIndex) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", x);
        jsonObject.addProperty("y", y);
        jsonObject.addProperty("z", z);
        jsonObject.addProperty("i", materialIndex);
        return jsonObject;
    }

    public static @NotNull BlockData fromJson(@NotNull JsonObject jsonObject, Material @NotNull [] materials) {
        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();
        int z = jsonObject.get("z").getAsInt();
        Material material = materials[jsonObject.get("i").getAsInt()];
        return new BlockData(x, y, z, material);
    }

    public void paste(@NotNull Location targetLocation, boolean applyPhysics) {
        World world = targetLocation.getWorld();
        Block block = Objects.requireNonNull(world).getBlockAt(
                targetLocation.getBlockX() + x,
                targetLocation.getBlockY() + y,
                targetLocation.getBlockZ() + z
        );
        block.setType(material, applyPhysics);
    }

    private BlockData(int x, int y, int z, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
