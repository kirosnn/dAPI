package fr.kirosnn.dAPI.world;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonSchematic implements FileType {

    private static final int START = '#';
    private int currentChar = START - 1;

    static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final Map<Short, String> chars = new HashMap<>();
    private final Map<Character, Short> controls = new HashMap<>();

    @Expose
    private int dataVersion;
    @Expose
    private String minecraftVersion;
    @Expose
    private List<Integer> dimensions;
    @Expose
    private List<String> palette;
    @Expose
    private String blocks;
    @Expose
    private Map<String, List<String>> waypoints;

    public JsonSchematic() {
    }

    public JsonSchematic(
            int dataVersion, String minecraftVersion,
            List<Integer> dimensions, List<String> palette,
            String blocks) {
        this.dataVersion = dataVersion;
        this.minecraftVersion = minecraftVersion;
        this.dimensions = dimensions;
        this.palette = palette;
        this.blocks = blocks;
        this.waypoints = new HashMap<>();
    }

    public JsonSchematic(
            int dataVersion, String minecraftVersion,
            List<Integer> dimensions, List<String> palette,
            String blocks, Map<String, List<String>> waypoints) {
        this.dataVersion = dataVersion;
        this.minecraftVersion = minecraftVersion;
        this.dimensions = dimensions;
        this.palette = palette;
        this.blocks = blocks;
        this.waypoints = waypoints;
    }

    @Override
    public boolean save(@NotNull Schematic schematic, @NotNull File file) {
        Preconditions.checkNotNull(schematic, "Schematic is null");
        Preconditions.checkNotNull(file, "File is null");

        Vector dimensionVector = schematic.getDimensions().subtract(new Vector(1, 1, 1));
        List<Integer> dimensions = List.of(dimensionVector.getBlockX(), dimensionVector.getBlockY(), dimensionVector.getBlockZ());
        List<String> palette = schematic.getPalette().stream().map(it -> it.getAsString(true)).collect(Collectors.toList());
        String serializedBlocks = String.join("", schematic.getBlocks().stream().map(this::getChar).collect(Collectors.toList()));
        Map<String, List<String>> waypoints = schematic.getWaypoints().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(it -> it.getX() + "," + it.getY() + "," + it.getZ() + "," + it.getYaw() + "," + it.getPitch())
                        .collect(Collectors.toList())));

        JsonSchematic jsonSchematic = new JsonSchematic(schematic.getDataVersion(), schematic.getMinecraftVersion(),
                dimensions, palette, serializedBlocks, waypoints);

        try {
            write(file, jsonSchematic);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    void write(File file, JsonSchematic type) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            GSON.toJson(type, writer);
            writer.flush();
        }
    }

    @Override
    @Nullable
    public Schematic load(@NotNull File file) {
        Preconditions.checkNotNull(file, "File is null");
        Preconditions.checkArgument(file.exists(), "File does not exist");

        try {
            JsonSchematic serialized = read(file);

            int dataVersion = serialized.dataVersion;
            String mcVersion = serialized.minecraftVersion;
            List<String> unparsedPalette = serialized.palette;

            List<BlockData> palette = unparsedPalette.stream()
                    .map(data -> (BlockData) Bukkit.createBlockData(data))
                    .collect(Collectors.toList());

            Vector dimensions = new Vector(serialized.dimensions.get(0),
                    serialized.dimensions.get(1),
                    serialized.dimensions.get(2));

            List<Short> blocks = serialized.blocks.chars()
                    .mapToObj(c -> fromChar((char) c))
                    .map(Short::valueOf)
                    .collect(Collectors.toList());

            Map<String, List<Location>> waypoints = new HashMap<>();
            if (dataVersion >= 2) {
                serialized.waypoints.forEach((key, locations) ->
                        waypoints.put(key, locations.stream()
                                .map(it -> it.split(","))
                                .map(it -> new Location(null, Double.parseDouble(it[0]),
                                        Double.parseDouble(it[1]),
                                        Double.parseDouble(it[2]),
                                        Float.parseFloat(it[3]),
                                        Float.parseFloat(it[4])))
                                .collect(Collectors.toList())));
            }

            return new Schematic(dataVersion, mcVersion, dimensions, palette, blocks, waypoints);

        } catch (IOException e) {
            return null;
        }
    }

    JsonSchematic read(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return GSON.fromJson(reader, JsonSchematic.class);
        }
    }

    String getChar(short id) {
        return chars.computeIfAbsent(id, it -> {
            do {
                currentChar++;
            } while (Character.isISOControl(currentChar));

            return Character.toString(currentChar);
        });
    }

    short fromChar(char c) {
        return controls.computeIfAbsent(c, it -> {
            int controlSince = 0;

            for (int i = START; i < c; i++) {
                if (Character.isISOControl(i)) {
                    controlSince++;
                }
            }

            return (short) (c - START - controlSince);
        });
    }
}