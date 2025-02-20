package fr.kirosnn.dAPI.schematic;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
 * The type Schematic.
 */
public final class Schematic {

    /**
     * The constant DATA_VERSION.
     */
    public static final int DATA_VERSION = 2;
    private final int dataVersion;
    private final String minecraftVersion;
    private final Vector dimensions;
    private final List<BlockData> palette;
    private final List<Short> blocks;
    private final Map<String, List<Location>> waypoints;

    /**
     * Instantiates a new Schematic.
     *
     * @param dataVersion      the data version
     * @param minecraftVersion the minecraft version
     * @param dimensions       the dimensions
     * @param palette          the palette
     * @param blocks           the blocks
     * @param waypoints        the waypoints
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
     * Instantiates a new Schematic.
     *
     * @param dataVersion      the data version
     * @param minecraftVersion the minecraft version
     * @param dimensions       the dimensions
     * @param palette          the palette
     * @param blocks           the blocks
     */
    public Schematic(int dataVersion, String minecraftVersion, Vector dimensions,
                     List<BlockData> palette, List<Short> blocks) {
        this(dataVersion, minecraftVersion, dimensions, palette, blocks, new HashMap<>());
    }

    /**
     * Create schematic.
     *
     * @param pos1 the pos 1
     * @param pos2 the pos 2
     * @return the schematic
     */
    @NotNull
    public static Schematic create(@NotNull Location pos1, @NotNull Location pos2) {
        Preconditions.checkArgument(pos1.getWorld() != null || pos2.getWorld() != null,
                "Locations must have at least one world");
        return create(pos1.getBlock(), pos2.getBlock());
    }

    /**
     * Create schematic.
     *
     * @param pos1 the pos 1
     * @param pos2 the pos 2
     * @return the schematic
     */
    @NotNull
    public static Schematic create(@NotNull Block pos1, @NotNull Block pos2) {
        Preconditions.checkArgument(pos1.getWorld() == pos2.getWorld(), "Blocks must be in the same world");

        BlocksData data = getBlocks(pos1, pos2, pos1.getWorld());

        return new Schematic(DATA_VERSION, Bukkit.getBukkitVersion().split("-")[0],
                data.dimensions, data.palette, data.blocks);
    }

    /**
     * Create schematic.
     *
     * @param pos1      the pos 1
     * @param pos2      the pos 2
     * @param waypoints the waypoints
     * @return the schematic
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
     * Create schematic.
     *
     * @param pos1      the pos 1
     * @param pos2      the pos 2
     * @param waypoints the waypoints
     * @return the schematic
     */
    @NotNull
    public static Schematic create(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Map<String, List<Location>> waypoints
    ) {
        World world = pos1.getWorld();

        BlocksData data = getBlocks(pos1, pos2, world);
        Block min = Vector.getMinimum(pos1.getLocation().toVector(), pos2.getLocation().toVector())
                .toLocation(world)
                .getBlock();

        Map<String, List<Location>> offsetWaypoints = waypoints.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    List<Location> locations = entry.getValue();
                    return new AbstractMap.SimpleEntry<>(name, locations.stream()
                            .map(location -> location.clone().subtract(min.getLocation()))
                            .collect(Collectors.toList()));
                })
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        return new Schematic(DATA_VERSION, Bukkit.getBukkitVersion().split("-")[0],
                data.dimensions, data.palette, data.blocks, offsetWaypoints);
    }

    /**
     * Create async completable future.
     *
     * @param pos1   the pos 1
     * @param pos2   the pos 2
     * @param plugin the plugin
     * @return the completable future
     */
    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Location pos1,
            @NotNull Location pos2,
            @NotNull Plugin plugin
    ) {
        CompletableFuture<Schematic> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(create(pos1, pos2)));

        return future;
    }

    /**
     * Create async completable future.
     *
     * @param pos1   the pos 1
     * @param pos2   the pos 2
     * @param plugin the plugin
     * @return the completable future
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
     * Create async completable future.
     *
     * @param pos1      the pos 1
     * @param pos2      the pos 2
     * @param waypoints the waypoints
     * @param plugin    the plugin
     * @return the completable future
     */
    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Location pos1,
            @NotNull Location pos2,
            @NotNull Map<String, List<Location>> waypoints,
            @NotNull Plugin plugin
    ) {
        CompletableFuture<Schematic> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(create(pos1, pos2, waypoints)));

        return future;
    }

    /**
     * Create async completable future.
     *
     * @param pos1      the pos 1
     * @param pos2      the pos 2
     * @param waypoints the waypoints
     * @param plugin    the plugin
     * @return the completable future
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
     * Load schematic.
     *
     * @param file the file
     * @param type the type
     * @return the schematic
     */
    @Nullable
    public static Schematic load(@NotNull File file, @NotNull FileType type) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkArgument(file.exists(), "File does not exist");

        return type.load(file);
    }

    /**
     * Load schematic.
     *
     * @param file the file
     * @param type the type
     * @return the schematic
     */
    @Nullable
    public static Schematic load(@NotNull String file, @NotNull FileType type) {
        return type.load(new File(file));
    }

    /**
     * Load schematic.
     *
     * @param file the file
     * @return the schematic
     */
    @Nullable
    public static Schematic load(@NotNull File file) {
        return load(file, new JsonSchematic());
    }

    /**
     * Load schematic.
     *
     * @param file the file
     * @return the schematic
     */
    @Nullable
    public static Schematic load(@NotNull String file) {
        return load(file, new JsonSchematic());
    }

    /**
     * Load async completable future.
     *
     * @param file   the file
     * @param type   the type
     * @param plugin the plugin
     * @return the completable future
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(
            @NotNull File file,
            @NotNull FileType type,
            @NotNull Plugin plugin
    ) {
        CompletableFuture<Schematic> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(load(file, type)));

        return future;
    }

    /**
     * Load async completable future.
     *
     * @param file   the file
     * @param type   the type
     * @param plugin the plugin
     * @return the completable future
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull String file, @NotNull FileType type, @NotNull Plugin plugin) {
        return loadAsync(new File(file), type, plugin);
    }

    /**
     * Load async completable future.
     *
     * @param file   the file
     * @param plugin the plugin
     * @return the completable future
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull File file, @NotNull Plugin plugin) {
        return loadAsync(file, new JsonSchematic(), plugin);
    }

    /**
     * Load async completable future.
     *
     * @param file   the file
     * @param plugin the plugin
     * @return the completable future
     */
    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull String file, @NotNull Plugin plugin) {
        return loadAsync(new File(file), plugin);
    }

    @Contract("_, _, _ -> new")
    private static @NotNull BlocksData getBlocks(Block pos1, Block pos2, @NotNull World world) {
        Preconditions.checkNotNull(pos1, "First position is null");
        Preconditions.checkNotNull(pos2, "Second position is null");

        Vector min = round(Vector.getMinimum(pos1.getLocation().toVector(), pos2.getLocation().toVector()));
        Vector max = round(Vector.getMaximum(pos1.getLocation().toVector(), pos2.getLocation().toVector()));
        Vector dimensions = max.clone().subtract(min);

        LinkedHashMap<BlockData, Short> paletteMap = new LinkedHashMap<>();
        LinkedList<Short> blocks = new LinkedList<>();

        Location pos = min.clone().toLocation(world);
        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            pos.setX(x);

            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                pos.setY(y);

                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    pos.setZ(z);

                    Block block = pos.getBlock();
                    BlockData type = block.getBlockData();

                    int size = paletteMap.size();
                    if (!paletteMap.containsKey(type)) {
                        paletteMap.put(type, (short) size);
                    }

                    blocks.add(paletteMap.get(type));
                }
            }
        }

        ArrayList<BlockData> palette = new ArrayList<>(paletteMap.keySet());

        return new BlocksData(dimensions, palette, blocks);
    }

    /**
     * Save boolean.
     *
     * @param file the file
     * @param type the type
     * @return the boolean
     */
    public boolean save(@NotNull File file, @NotNull FileType type) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkNotNull(type, "File type is null");

        return type.save(this, file);
    }

    /**
     * Save boolean.
     *
     * @param file the file
     * @param type the type
     * @return the boolean
     */
    public boolean save(@NotNull String file, @NotNull FileType type) {
        return save(new File(file), type);
    }

    /**
     * Save boolean.
     *
     * @param file the file
     * @return the boolean
     */
    public boolean save(@NotNull File file) {
        return save(file, new JsonSchematic());
    }

    /**
     * Save boolean.
     *
     * @param file the file
     * @return the boolean
     */
    public boolean save(@NotNull String file) {
        return save(new File(file));
    }

    /**
     * Save async completable future.
     *
     * @param file   the file
     * @param type   the type
     * @param plugin the plugin
     * @return the completable future
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull File file, @NotNull FileType type, @NotNull Plugin plugin) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(save(file, type)));

        return future;
    }

    /**
     * Save async completable future.
     *
     * @param file   the file
     * @param type   the type
     * @param plugin the plugin
     * @return the completable future
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull String file, @NotNull FileType type, @NotNull Plugin plugin) {
        return saveAsync(new File(file), type, plugin);
    }

    /**
     * Save async completable future.
     *
     * @param file   the file
     * @param plugin the plugin
     * @return the completable future
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull File file, @NotNull Plugin plugin) {
        return saveAsync(file, new JsonSchematic(), plugin);
    }

    /**
     * Save async completable future.
     *
     * @param file   the file
     * @param plugin the plugin
     * @return the completable future
     */
    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull String file, @NotNull Plugin plugin) {
        return saveAsync(new File(file), plugin);
    }

    /**
     * Paste list.
     *
     * @param location the location
     * @param skipAir  the skip air
     * @return the list
     */
    public @NotNull List<Block> paste(@NotNull Location location, boolean skipAir) {
        return paste(location.getBlock(), skipAir);
    }

    /**
     * Paste list.
     *
     * @param block   the block
     * @param skipAir the skip air
     * @return the list
     */
    public @NotNull List<Block> paste(@NotNull Block block, boolean skipAir) {
        Preconditions.checkNotNull(block, "Block is null");

        Location pos = block.getLocation();
        Location max = pos.clone().add(dimensions);
        ArrayList<Block> bs = new ArrayList<>();

        int idx = 0;

        for (int x = block.getX(); x <= max.getBlockX(); x++) {
            for (int y = block.getY(); y <= max.getBlockY(); y++) {
                for (int z = block.getZ(); z <= max.getBlockZ(); z++) {
                    Block loopBlock = block.getWorld().getBlockAt(x, y, z);
                    loopBlock.setType(Material.AIR);
                }
            }
        }

        for (int x = block.getX(); x <= max.getBlockX(); x++) {
            pos.setX(x);

            for (int y = block.getY(); y <= max.getBlockY(); y++) {
                pos.setY(y);

                for (int z = block.getZ(); z <= max.getBlockZ(); z++) {
                    pos.setZ(z);

                    BlockData data = palette.get(blocks.get(idx));

                    if (skipAir && data.getMaterial().isAir()) {
                        idx++;
                        continue;
                    }

                    Block loopBlock = pos.getBlock();
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
     * Gets waypoints.
     *
     * @param pastedAt the pasted at
     * @param name     the name
     * @return the waypoints
     */
    @Nullable
    public List<Location> getWaypoints(@NotNull Location pastedAt, @NotNull String name) {
        return getWaypoints(pastedAt.getBlock(), name);
    }

    /**
     * Gets waypoints.
     *
     * @param pastedAt the pasted at
     * @param name     the name
     * @return the waypoints
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
                .collect(Collectors.toList());
    }

    /**
     * Gets waypoint.
     *
     * @param pastedAt the pasted at
     * @param name     the name
     * @return the waypoint
     */
    @Nullable
    public Location getWaypoint(@NotNull Location pastedAt, @NotNull String name) {
        return getWaypoint(pastedAt.getBlock(), name);
    }

    /**
     * Gets waypoint.
     *
     * @param pastedAt the pasted at
     * @param name     the name
     * @return the waypoint
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
     * Gets data version.
     *
     * @return the data version
     */
    public int getDataVersion() {
        return dataVersion;
    }

    /**
     * Gets minecraft version.
     *
     * @return the minecraft version
     */
    @NotNull
    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    /**
     * Gets dimensions.
     *
     * @return the dimensions
     */
    @NotNull
    public Vector getDimensions() {
        return dimensions.clone().add(new Vector(1, 1, 1));
    }

    /**
     * Gets palette.
     *
     * @return the palette
     */
    @NotNull
    @UnmodifiableView
    public List<BlockData> getPalette() {
        return Collections.unmodifiableList(palette);
    }

    /**
     * Gets blocks.
     *
     * @return the blocks
     */
    @NotNull
    @UnmodifiableView
    public List<Short> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    /**
     * Gets waypoints.
     *
     * @return the waypoints
     */
    @NotNull
    @UnmodifiableView
    public Map<String, List<Location>> getWaypoints() {
        return Collections.unmodifiableMap(waypoints);
    }

    /**
     * Data version int.
     *
     * @return the int
     */
    public int dataVersion() {
        return dataVersion;
    }

    /**
     * Minecraft version string.
     *
     * @return the string
     */
    public String minecraftVersion() {
        return minecraftVersion;
    }

    /**
     * Dimensions vector.
     *
     * @return the vector
     */
    public Vector dimensions() {
        return dimensions;
    }

    /**
     * Palette list.
     *
     * @return the list
     */
    public List<BlockData> palette() {
        return palette;
    }

    /**
     * Blocks list.
     *
     * @return the list
     */
    public List<Short> blocks() {
        return blocks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Schematic that = (Schematic) obj;

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

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Schematic[" +
                "dataVersion=" + dataVersion + ", " +
                "minecraftVersion=" + minecraftVersion + ", " +
                "dimensions=" + dimensions + ", " +
                "palette=" + palette + ", " +
                "blocks=" + blocks + ']';
    }

    /**
     * The type Blocks data.
     */
    public static class BlocksData {
        private final Vector dimensions;
        private final List<BlockData> palette;
        private final List<Short> blocks;

        /**
         * Instantiates a new Blocks data.
         *
         * @param dimensions the dimensions
         * @param palette    the palette
         * @param blocks     the blocks
         */
        public BlocksData(Vector dimensions, List<BlockData> palette, List<Short> blocks) {
            this.dimensions = dimensions;
            this.palette = palette;
            this.blocks = blocks;
        }

        /**
         * Gets dimensions.
         *
         * @return the dimensions
         */
        public Vector getDimensions() {
            return dimensions;
        }

        /**
         * Gets palette.
         *
         * @return the palette
         */
        public List<BlockData> getPalette() {
            return palette;
        }

        /**
         * Gets blocks.
         *
         * @return the blocks
         */
        public List<Short> getBlocks() {
            return blocks;
        }
    }
}