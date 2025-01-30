package fr.kirosnn.dAPI.world;

import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

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

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", x);
        jsonObject.addProperty("y", y);
        jsonObject.addProperty("z", z);
        jsonObject.addProperty("material", material.name());
        return jsonObject;
    }

    public static @NotNull BlockData fromJson(@NotNull JsonObject jsonObject) {
        int x = jsonObject.get("x").getAsInt();
        int y = jsonObject.get("y").getAsInt();
        int z = jsonObject.get("z").getAsInt();
        Material material = Material.valueOf(jsonObject.get("material").getAsString());
        return new BlockData(x, y, z, material);
    }

    public Block paste(@NotNull Location targetLocation, boolean applyPhysics) {
        World world = targetLocation.getWorld();
        Block block = Objects.requireNonNull(world).getBlockAt(
                targetLocation.getBlockX() + x,
                targetLocation.getBlockY() + y,
                targetLocation.getBlockZ() + z
        );
        block.setType(material, applyPhysics);
        return block;
    }

    private BlockData(int x, int y, int z, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
    }
}
