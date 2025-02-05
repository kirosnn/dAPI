package fr.kirosnn.dAPI.world;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Manages chunks and applies dynamic conditions.
 */
public class ChunkManager {

    private final Map<String, ChunkCondition> chunkConditions = new HashMap<>();

    /**
     * Initializes the chunk manager.
     *
     * @param plugin The Bukkit plugin.
     */
    public ChunkManager(@NotNull Plugin plugin) {
    }

    /**
     * Adds or updates a condition for a chunk.
     *
     * @param chunk     The chunk.
     * @param condition The condition to apply.
     */
    public void setChunkCondition(@NotNull Chunk chunk, @NotNull Predicate<Chunk> condition) {
        String key = getChunkKey(chunk);
        chunkConditions.put(key, new ChunkCondition(chunk, condition));
    }

    /**
     * Checks if a chunk meets its assigned condition.
     *
     * @param chunk The chunk to check.
     * @return True if the condition is met, false otherwise.
     */
    public boolean checkChunkCondition(@NotNull Chunk chunk) {
        String key = getChunkKey(chunk);
        ChunkCondition chunkCondition = chunkConditions.get(key);
        return chunkCondition != null && chunkCondition.condition().test(chunk);
    }

    /**
     * Removes a chunk condition.
     *
     * @param chunk The chunk to remove.
     */
    public void removeChunkCondition(@NotNull Chunk chunk) {
        chunkConditions.remove(getChunkKey(chunk));
    }

    /**
     * Clears all stored chunk conditions.
     */
    public void clearAllChunkConditions() {
        chunkConditions.clear();
    }

    /**
     * Gets the exact world coordinates of a chunk.
     *
     * @param chunk The chunk.
     * @return The location of the chunk's bottom-left corner.
     */
    public Location getChunkCoordinates(@NotNull Chunk chunk) {
        int chunkX = chunk.getX() * 16;
        int chunkZ = chunk.getZ() * 16;
        World world = chunk.getWorld();
        return new Location(world, chunkX, world.getMinHeight(), chunkZ);
    }

    /**
     * Gets the exact world coordinates of a block in a chunk.
     *
     * @param chunk The chunk.
     * @param x     The block's x coordinate inside the chunk (0-15).
     * @param y     The block's y coordinate.
     * @param z     The block's z coordinate inside the chunk (0-15).
     * @return The world location of the block.
     */
    public Location getBlockInChunk(@NotNull Chunk chunk, int x, int y, int z) {
        if (x < 0 || x >= 16 || z < 0 || z >= 16) {
            throw new IllegalArgumentException("Block coordinates must be between 0 and 15 inside a chunk.");
        }
        World world = chunk.getWorld();
        int worldX = (chunk.getX() * 16) + x;
        int worldZ = (chunk.getZ() * 16) + z;
        return new Location(world, worldX, y, worldZ);
    }

    /**
     * Returns the unique key for a chunk.
     *
     * @param chunk The chunk.
     * @return The chunk key.
     */
    private @NotNull String getChunkKey(@NotNull Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }

    /**
         * Internal class for storing chunk conditions.
         */
        private record ChunkCondition(Chunk chunk, Predicate<Chunk> condition) {
    }
}
