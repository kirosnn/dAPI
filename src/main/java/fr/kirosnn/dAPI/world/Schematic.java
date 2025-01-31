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

public final class Schematic {

    public static final int DATA_VERSION = 2;
    private final int dataVersion;
    private final String minecraftVersion;
    private final Vector dimensions;
    private final List<BlockData> palette;
    private final List<Short> blocks;
    private final Map<String, List<Location>> waypoints;

    public Schematic(int dataVersion, String minecraftVersion, Vector dimensions,
                     List<BlockData> palette, List<Short> blocks, Map<String, List<Location>> waypoints) {
        this.dataVersion = dataVersion;
        this.minecraftVersion = minecraftVersion;
        this.dimensions = dimensions;
        this.palette = palette;
        this.blocks = blocks;
        this.waypoints = waypoints;
    }

    public Schematic(int dataVersion, String minecraftVersion, Vector dimensions,
                     List<BlockData> palette, List<Short> blocks) {
        this(dataVersion, minecraftVersion, dimensions, palette, blocks, new HashMap<>());
    }

    @NotNull
    public static Schematic create(@NotNull Location pos1, @NotNull Location pos2) {
        Preconditions.checkArgument(pos1.getWorld() != null || pos2.getWorld() != null,
                "Locations must have at least one world");
        return create(pos1.getBlock(), pos2.getBlock());
    }

    @NotNull
    public static Schematic create(@NotNull Block pos1, @NotNull Block pos2) {
        Preconditions.checkArgument(pos1.getWorld() == pos2.getWorld(), "Blocks must be in the same world");

        @NotNull BlocksData data = getBlocks(pos1, pos2, pos1.getWorld());

        return new Schematic(DATA_VERSION, Bukkit.getBukkitVersion().split("-")[0],
                data.dimensions, data.palette, data.blocks);
    }

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

    @NotNull
    public static Schematic create(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Map<String, List<Location>> waypoints
    ) {
        World world = pos1.getWorld();

        @NotNull BlocksData data = getBlocks(pos1, pos2, world);
        Block min = Vector.getMinimum(pos1.getLocation().toVector(), pos2.getLocation().toVector())
                .toLocation(world)
                .getBlock();

        Map<String, List<Location>> offsetWaypoints = new HashMap<>();
        for (Map.Entry<String, List<Location>> entry : waypoints.entrySet()) {
            List<Location> updatedLocations = new ArrayList<>();
            for (Location location : entry.getValue()) {
                updatedLocations.add(location.clone().subtract(min.getLocation()));
            }
            offsetWaypoints.put(entry.getKey(), updatedLocations);
        }

        return new Schematic(DATA_VERSION, Bukkit.getBukkitVersion().split("-")[0],
                data.dimensions, data.palette, data.blocks, offsetWaypoints);
    }

    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Location pos1,
            @NotNull Location pos2,
            @NotNull Plugin plugin
    ) {
        final CompletableFuture<Schematic> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(create(pos1.getBlock(), pos2.getBlock(), new HashMap<>())));

        return future;
    }

    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Plugin plugin
    ) {
        return createAsync(pos1.getLocation(), pos2.getLocation(), plugin);
    }

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

    @NotNull
    public static CompletableFuture<Schematic> createAsync(
            @NotNull Block pos1,
            @NotNull Block pos2,
            @NotNull Map<String, List<Location>> waypoints,
            @NotNull Plugin plugin
    ) {
        return createAsync(pos1.getLocation(), pos2.getLocation(), waypoints, plugin);
    }

    @Nullable
    public static Schematic load(@NotNull File file, @NotNull FileType type) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkArgument(file.exists(), "File does not exist");

        return type.load(file);
    }

    @Nullable
    public static Schematic load(@NotNull String file, @NotNull FileType type) {
        return type.load(new File(file));
    }

    @Nullable
    public static Schematic load(@NotNull File file) {
        return load(file, new JsonSchematic());
    }

    @Nullable
    public static Schematic load(@NotNull String file) {
        return load(file, new JsonSchematic());
    }

    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull File file, @NotNull FileType type, @NotNull Plugin plugin) {
        CompletableFuture<Schematic> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(load(file, type)));

        return future;
    }

    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull String file, @NotNull FileType type, @NotNull Plugin plugin) {
        return loadAsync(new File(file), type, plugin);
    }

    public static @NotNull CompletableFuture<Schematic> loadAsync(@NotNull File file, @NotNull Plugin plugin) {
        return loadAsync(file, new JsonSchematic(), plugin);
    }

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

        Map<BlockData, Short> paletteMap = new LinkedHashMap<>();
        List<Short> blocks = new LinkedList<>();

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

        List<BlockData> palette = new ArrayList<>(paletteMap.keySet());

        return new BlocksData(dimensions, palette, blocks);
    }

    public boolean save(@NotNull File file, @NotNull FileType type) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkNotNull(type, "File type is null");

        return type.save(this, file);
    }

    public boolean save(@NotNull String file, @NotNull FileType type) {
        return save(new File(file), type);
    }

    public boolean save(@NotNull File file) {
        return save(file, new JsonSchematic());
    }

    public boolean save(@NotNull String file) {
        return save(new File(file));
    }

    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull File file, @NotNull FileType type, @NotNull Plugin plugin) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> future.complete(save(file, type)));

        return future;
    }

    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull String file, @NotNull FileType type, @NotNull Plugin plugin) {
        return saveAsync(new File(file), type, plugin);
    }

    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull File file, @NotNull Plugin plugin) {
        return saveAsync(file, new JsonSchematic(), plugin);
    }

    @NotNull
    public CompletableFuture<Boolean> saveAsync(@NotNull String file, @NotNull Plugin plugin) {
        return saveAsync(new File(file), plugin);
    }

    public @NotNull List<Block> paste(@NotNull Location location, boolean skipAir) {
        return paste(location.getBlock(), skipAir);
    }

    public @NotNull List<Block> paste(@NotNull Block block, boolean skipAir) {
        Preconditions.checkNotNull(block, "Block is null");

        Location pos = block.getLocation();
        Location max = pos.clone().add(dimensions);
        List<Block> bs = new ArrayList<>();

        int idx = 0;
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

    @Nullable
    public List<Location> getWaypoints(@NotNull Location pastedAt, @NotNull String name) {
        return getWaypoints(pastedAt.getBlock(), name);
    }

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

    @Nullable
    public Location getWaypoint(@NotNull Location pastedAt, @NotNull String name) {
        return getWaypoint(pastedAt.getBlock(), name);
    }

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

    public int getDataVersion() {
        return dataVersion;
    }

    @NotNull
    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    @NotNull
    public Vector getDimensions() {
        return dimensions.clone().add(new Vector(1, 1, 1));
    }

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

    private record BlocksData(Vector dimensions, List<BlockData> palette, List<Short> blocks) {

    }
}