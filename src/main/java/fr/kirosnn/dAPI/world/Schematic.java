package fr.kirosnn.dAPI.world;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Creates a new {@link Schematic} instance. This should only be used for custom {@link FileType} implementations.
 *
 * @see #create(Location, Location)
 */
public final class Schematic {

    public static final int DATA_VERSION = 2;
    private final int dataVersion;
    private final String minecraftVersion;
    private final Vector dimensions;
    private final List<BlockData> palette;
    private final List<Short> blocks;
    private final Map<String, List<Location>> waypoints;

    /**
     * @param dataVersion      The data version.
     * @param minecraftVersion The Minecraft version.
     * @param dimensions       The dimensions of the schematic.
     * @param palette          The palette of block data.
     * @param blocks           The block data.
     * @param waypoints        The waypoints.
     */
    public Schematic(int dataVersion, String minecraftVersion, Vector dimensions,
                     List<BlockData> palette, List<Short> blocks, Map<String, List<Location>> waypoints) {
        this.dataVersion = dataVersion;
        this.minecraftVersion = minecraftVersion;
        this.dimensions = dimensions;
        this.palette = palette;
        this.blocks = blocks;
        this.waypoints = waypoints;
    }

    /**
     * @param dataVersion      The data version.
     * @param minecraftVersion The Minecraft version.
     * @param dimensions       The dimensions of the schematic.
     * @param palette          The palette of block data.
     * @param blocks           The block data.
     */
    public Schematic(int dataVersion, String minecraftVersion, Vector dimensions,
                     List<BlockData> palette, List<Short> blocks) {
        this(dataVersion, minecraftVersion, dimensions, palette, blocks, new HashMap<>());
    }

    /**
     * Synchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * For large schematics, use {@link #createAsync(Location, Location, Plugin)}.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @return A new {@link Schematic} instance.
     */
    @NotNull
    public static Schematic create(@NotNull Location pos1, @NotNull Location pos2) {
        Preconditions.checkArgument(pos1.getWorld() != null || pos2.getWorld() != null,
                "Locations must have at least one world");
        return create(pos1.getBlock(), pos2.getBlock());
    }

    /**
     * Synchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * For large schematics, use {@link #createAsync(Block, Block, Plugin)}.
     *
     * @param pos1 The first block.
     * @param pos2 The second block.
     * @return A new {@link Schematic} instance.
     */
    @NotNull
    public static Schematic create(@NotNull Block pos1, @NotNull Block pos2) {
        Preconditions.checkArgument(pos1.getWorld() == pos2.getWorld(), "Blocks must be in the same world");

        var data = getBlocks(pos1, pos2, pos1.getWorld());

        return new Schematic(DATA_VERSION, Bukkit.getBukkitVersion().split("-")[0],
                data.dimensions, data.palette, data.blocks);
    }

    /**
     * Synchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * For large schematics, use {@link #createAsync(Location, Location, Plugin)}.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @param waypoints A map of waypoints, where each key identifies a vector offset from the paste location.
     * @return A new {@link Schematic} instance.
     */
    @NotNull
    public static Schematic create(
            @NotNull Location pos1,
            @NotNull Location pos2,
            @NotNull Map<String, List<Location>> waypoints
    ) {
        Preconditions.checkArgument(pos1.getWorld() != null || pos2.getWorld() != null,
                "Locations must have at least one world");
        return create(pos1.getBlock(), pos2.getBlock(), waypoints);
    }

    /**
     * Synchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * For large schematics, use {@link #createAsync(Block, Block, Plugin)}.
     *
     * @param pos1 The first block.
     * @param pos2 The second block.
     * @param waypoints A map of waypoints, where each key identifies a vector offset from the paste location.
     * @return A new {@link Schematic} instance.
     */
    @NotNull
    public static Schematic create(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Map<String, List<Location>> waypoints
    ) {
        var world = pos1.getWorld();

        var data = getBlocks(pos1, pos2, world);
        var min = Vector.getMinimum(pos1.getLocation().toVector(), pos2.getLocation().toVector())
                .toLocation(world)
                .getBlock();

        var offsetWaypoints = waypoints.entrySet().stream()
                .map(entry -> {
                    var name = entry.getKey();
                    var locations = entry.getValue();
                    return Map.entry(name, locations.stream()
                            .map(location -> location.clone().subtract(min.getLocation()))
                            .toList());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Schematic(DATA_VERSION, Bukkit.getBukkitVersion().split("-")[0],
                data.dimensions, data.palette, data.blocks, offsetWaypoints);
    }

    /**
     * Asynchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * This method avoids blocking the main thread during block fetching.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned.
     */
    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Location pos1,
            @NotNull Location pos2,
            @NotNull Plugin plugin
    ) {
        var future = new CompletableFuture<Schematic>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(create(pos1, pos2)));

        return future;
    }

    /**
     * Asynchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * This method avoids blocking the main thread during block fetching.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned.
     */
    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Plugin plugin
    ) {
        return createAsync(pos1.getLocation(), pos2.getLocation(), plugin);
    }

    /**
     * Asynchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * This method avoids blocking the main thread during block fetching.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @param waypoints A map of waypoints, where each key identifies a vector offset from the paste location.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned.
     */
    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Location pos1,
            @NotNull Location pos2,
            @NotNull Map<String, List<Location>> waypoints,
            @NotNull Plugin plugin
    ) {
        var future = new CompletableFuture<Schematic>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(create(pos1, pos2, waypoints)));

        return future;
    }

    /**
     * Asynchronously gets and stores all blocks between the positions in a new {@link Schematic} instance.
     * This method avoids blocking the main thread during block fetching.
     *
     * @param pos1 The first position.
     * @param pos2 The second position.
     * @param waypoints A map of waypoints, where each key identifies a vector offset from the paste location.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned.
     */
    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Map<String, List<Location>> waypoints,
            @NotNull Plugin plugin
    ) {
        return createAsync(pos1.getLocation(), pos2.getLocation(), waypoints, plugin);
    }

    /**
     * Reads a schematic from a file with the specified {@link FileType}.
     * For large schematics, use {@link #loadAsync(File, FileType, Plugin)}.
     *
     * @param file The file to read.
     * @param type The {@link FileType} instance.
     * @return A new {@link Schematic} instance, or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    @Nullable
    public static Schematic load(@NotNull File file, @NotNull FileType type) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkArgument(file.exists(), "File does not exist");

        return type.load(file);
    }

    /**
     * Loads a schematic from a file with the specified {@link FileType}.
     * For large schematics, use {@link #loadAsync(File, FileType, Plugin)}.
     *
     * @param file The file to read.
     * @param type The {@link FileType} instance.
     * @return A new {@link Schematic} instance, or null if reading fails or if the file doesn't exist.
     */
    @Nullable
    public static Schematic load(@NotNull String file, @NotNull FileType type) {
        return type.load(new File(file));
    }

    /**
     * Reads a schematic from a file with the default {@link JsonSchematic}.
     * For large schematics, use {@link #loadAsync(File, Plugin)}.
     *
     * @param file The file to read.
     * @return A new {@link Schematic} instance, or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    @Nullable
    public static Schematic load(@NotNull File file) {
        return load(file, new JsonSchematic());
    }

    /**
     * Reads a schematic from a file with the default {@link JsonSchematic}.
     * For large schematics, use {@link #loadAsync(File, Plugin)}.
     *
     * @param file The file to read.
     * @return A new {@link Schematic} instance, or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    @Nullable
    public static Schematic load(@NotNull String file) {
        return load(file, new JsonSchematic());
    }

    /**
     * Asynchronously reads a schematic from a file with the specified {@link FileType}.
     *
     * @param file The file to read.
     * @param type The {@link FileType} instance.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned,
     * or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull File file, @NotNull FileType type, @NotNull Plugin plugin) {
        var future = new CompletableFuture<Schematic>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(load(file, type)));

        return future;
    }

    /**
     * Asynchronously reads a schematic from a file with the specified {@link FileType}.
     *
     * @param file The file to read.
     * @param type The {@link FileType} instance.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned,
     * or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull String file, @NotNull FileType type, @NotNull Plugin plugin) {
        return loadAsync(new File(file), type, plugin);
    }

    /**
     * Asynchronously reads a schematic from a file with the default {@link JsonSchematic}.
     *
     * @param file The file to read.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned,
     * or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull File file, @NotNull Plugin plugin) {
        return loadAsync(file, new JsonSchematic(), plugin);
    }

    /**
     * Asynchronously reads a schematic from a file with the default {@link JsonSchematic}.
     *
     * @param file The file to read.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}. When completed, the new {@link Schematic} instance is returned,
     * or null if reading fails or if the file doesn't exist.
     * @throws IllegalArgumentException If file does not exist or is null.
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull String file, @NotNull Plugin plugin) {
        return loadAsync(new File(file), plugin);
    }

    @Contract("_, _, _ -> new")
    private static @NotNull BlocksData getBlocks(Block pos1, Block pos2, @NotNull World world) {
        Preconditions.checkNotNull(pos1, "First position is null");
        Preconditions.checkNotNull(pos2, "Second position is null");

        var min = round(Vector.getMinimum(pos1.getLocation().toVector(), pos2.getLocation().toVector()));
        var max = round(Vector.getMaximum(pos1.getLocation().toVector(), pos2.getLocation().toVector()));
        var dimensions = max.clone().subtract(min);

        var paletteMap = new LinkedHashMap<BlockData, Short>();
        var blocks = new LinkedList<Short>();

        var pos = min.clone().toLocation(world);
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            pos.setX(x);

            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                pos.setY(y);

                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    pos.setZ(z);

                    var block = pos.getBlock();
                    var type = block.getBlockData();

                    var size = paletteMap.size();
                    if (!paletteMap.containsKey(type)) {
                        paletteMap.put(type, (short) size);
                    }

                    blocks.add(paletteMap.get(type));
                }
            }
        }

        var palette = new ArrayList<>(paletteMap.keySet());

        return new BlocksData(dimensions, palette, blocks);
    }

    /**
     * Saves the schematic to a file with the specified {@link FileType}.
     * For large schematics, use {@link #saveAsync(File, FileType, Plugin)}.
     *
     * @param file The file to save to.
     * @param type The {@link FileType} instance.
     * @return {@code true} if the schematic was saved successfully, false if an error was returned.
     * @throws IllegalArgumentException If file or filetype is null.
     */
    public boolean save(@NotNull File file, @NotNull FileType type) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkNotNull(type, "File type is null");

        return type.save(this, file);
    }

    /**
     * Saves the schematic to a file with the specified {@link FileType}.
     * For large schematics, use {@link #saveAsync(File, FileType, Plugin)}.
     *
     * @param file The file to save to.
     * @param type The {@link FileType} instance.
     * @return {@code true} if the schematic was saved successfully, false if an error was returned.
     * @throws IllegalArgumentException If file or filetype is null.
     */
    public boolean save(@NotNull String file, @NotNull FileType type) {
        return save(new File(file), type);
    }

    /**
     * Saves the schematic to a file with the default {@link JsonSchematic}.
     * For large schematics, use {@link #saveAsync(File, Plugin)}.
     *
     * @param file The file to save to.
     * @return {@code true} if the schematic was saved successfully, false if an error was returned.
     * @throws IllegalArgumentException If file is null.
     */
    public boolean save(@NotNull File file) {
        return save(file, new JsonSchematic());
    }

    /**
     * Saves the schematic to a file with the default {@link JsonSchematic}.
     * For large schematics, use {@link #saveAsync(File, Plugin)}.
     *
     * @param file The file to save to.
     * @return {@code true} if the schematic was saved successfully, false if an error was returned.
     * @throws IllegalArgumentException If file is null.
     */
    public boolean save(@NotNull String file) {
        return save(new File(file));
    }

    /**
     * Asynchronously saves the schematic to a file with the specified {@link FileType}.
     *
     * @param file The file to save to.
     * @param type The {@link FileType} instance.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}.
     * When completed, {@code true} if the schematic was saved successfully, false if an error was returned.
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull File file, @NotNull FileType type, @NotNull Plugin plugin) {
        var future = new CompletableFuture<Boolean>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(save(file, type)));

        return future;
    }

    /**
     * Asynchronously saves the schematic to a file with the specified {@link FileType}.
     *
     * @param file The file to save to.
     * @param type The {@link FileType} instance.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}.
     * When completed, {@code true} if the schematic was saved successfully, false if an error was returned.
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull String file, @NotNull FileType type, @NotNull Plugin plugin) {
        return saveAsync(new File(file), type, plugin);
    }

    /**
     * Asynchronously saves the schematic to a file with the default {@link JsonSchematic}.
     *
     * @param file The file to save to.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}.
     * When completed, {@code true} if the schematic was saved successfully, false if an error was returned.
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull File file, @NotNull Plugin plugin) {
        return saveAsync(file, new JsonSchematic(), plugin);
    }

    /**
     * Asynchronously saves the schematic to a file with the default {@link JsonSchematic}.
     *
     * @param file The file to save to.
     * @param plugin The plugin instance.
     * @return A {@link CompletableFuture}.
     * When completed, {@code true} if the schematic was saved successfully, false if an error was returned.
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull String file, @NotNull Plugin plugin) {
        return saveAsync(new File(file), plugin);
    }

    /**
     * Pastes the schematic at the specified location.
     *
     * @param location The location to paste the schematic at.
     * @return A list of all blocks which have been altered.
     */
    public @NotNull List<Block> paste(@NotNull Location location, boolean skipAir) {
        return paste(location.getBlock(), skipAir);
    }

    /**
     * Pastes the schematic at the specified block.
     *
     * @param block The block to paste the schematic at.
     * @return A list of all blocks which have been altered.
     */
    public @NotNull List<Block> paste(@NotNull Block block, boolean skipAir) {
        Preconditions.checkNotNull(block, "Block is null");

        var pos = block.getLocation();
        var max = pos.clone().add(dimensions);
        var bs = new ArrayList<Block>();

        var idx = 0;
        for (int x = block.getX(); x <= max.getBlockX(); x++) {
            pos.setX(x);

            for (int y = block.getY(); y <= max.getBlockY(); y++) {
                pos.setY(y);

                for (int z = block.getZ(); z <= max.getBlockZ(); z++) {
                    pos.setZ(z);

                    var data = palette.get(blocks.get(idx));

                    if (skipAir && data.getMaterial().isAir()) {
                        idx++;
                        continue;
                    }

                    var loopBlock = pos.getBlock();
                    loopBlock.setBlockData(data);
                    bs.add(loopBlock);

                    idx++;
                }
            }
        }

        return bs;
    }

    @Contract("_ -> new")
    private static @NotNull Vector round(@NotNull Vector vector) {
        return new Vector(Math.floor(vector.getX()), Math.floor(vector.getY()), Math.floor(vector.getZ()));
    }

    /**
     * Returns the absolute locations of specific waypoints, not relative to the paste location.
     * If the waypoints do not exist, null is returned.
     *
     * @param pastedAt The location where the schematic was pasted.
     * @param name The name of the waypoint.
     * @return The absolute location of the waypoints, or null if the waypoints do not exist.
     */
    @Nullable
    public List<Location> getWaypoints(@NotNull Location pastedAt, @NotNull String name) {
        return getWaypoints(pastedAt.getBlock(), name);
    }

    /**
     * Returns the absolute locations of specific waypoints, not relative to the paste location.
     * If the waypoints do not exist, null is returned.
     *
     * @param pastedAt The block where the schematic was pasted.
     * @param name The name of the waypoint.
     * @return The absolute location of the waypoints, or null if the waypoints do not exist.
     */
    @Nullable
    public List<Location> getWaypoints(@NotNull Block pastedAt, @NotNull String name) {
        Preconditions.checkNotNull(pastedAt.getWorld(), "World is null");

        if (!waypoints.containsKey(name)) {
            return null;
        }

        return waypoints.get(name).stream()
                .map(location -> {
                    Location added = location.clone();
                    added.setWorld(pastedAt.getWorld());
                    return added.add(pastedAt.getLocation());
                })
                .toList();
    }

    /**
     * Returns the absolute location of a specific waypoint, not relative to the paste location.
     * If the waypoint does not exist, null is returned.
     *
     * @param pastedAt The location where the schematic was pasted.
     * @param name The name of the waypoint.
     * @return The absolute location of the waypoint, or null if the waypoint does not exist.
     */
    @Nullable
    public Location getWaypoint(@NotNull Location pastedAt, @NotNull String name) {
        return getWaypoint(pastedAt.getBlock(), name);
    }

    /**
     * Returns the absolute location of a specific waypoint, not relative to the paste location.
     * If the waypoint does not exist, null is returned.
     *
     * @param pastedAt The block where the schematic was pasted.
     * @param name The name of the waypoint.
     * @return The absolute location of the waypoint, or null if the waypoint does not exist.
     */
    @Nullable
    public Location getWaypoint(@NotNull Block pastedAt, @NotNull String name) {
        Preconditions.checkNotNull(pastedAt.getWorld(), "World is null");

        if (!waypoints.containsKey(name)) {
            return null;
        }

        Location location = waypoints.get(name).get(0);

        if (location == null) {
            return null;
        }

        Location added = location.clone();
        added.setWorld(pastedAt.getWorld());
        return added.clone().add(pastedAt.getLocation());
    }

    /**
     * @return The data version this schematic was saved in.
     */
    public int getDataVersion() {
        return dataVersion;
    }

    /**
     * @return The Minecraft version this schematic was saved in.
     */
    @NotNull
    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    /**
     * @return The dimensions of the schematic.
     */
    @NotNull
    public Vector getDimensions() {
        return dimensions.clone().add(new Vector(1, 1, 1));
    }

    /**
     * @return The palette of block data.
     */
    @NotNull
    @UnmodifiableView
    public List<BlockData> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    @NotNull
    @UnmodifiableView
    public List<Short> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    @NotNull
    @UnmodifiableView
    public Map<String, List<Location>> getWaypoints() {
        return Collections.unmodifiableMap(waypoints);
    }

    public int dataVersion() {
        return dataVersion;
    }

    public String minecraftVersion() {
        return minecraftVersion;
    }

    public Vector dimensions() {
        return dimensions;
    }

    public List<BlockData> palette() {
        return palette;
    }

    public List<Short> blocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Schematic) obj;

        return this.dataVersion == that.dataVersion &&
                Objects.equals(this.minecraftVersion, that.minecraftVersion) &&
                Objects.equals(this.dimensions, that.dimensions) &&
                Objects.equals(this.palette, that.palette) &&
                Objects.equals(this.blocks, that.blocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataVersion, minecraftVersion, dimensions, palette, blocks);
    }

    @Override
    public String toString() {
        return "Schematic[" +
                "dataVersion=" + dataVersion + ", " +
                "minecraftVersion=" + minecraftVersion + ", " +
                "dimensions=" + dimensions + ", " +
                "palette=" + palette + ", " +
                "blocks=" + blocks + ']';
    }

    private record BlocksData(Vector dimensions, List<BlockData> palette, List<Short> blocks) {

    }
}