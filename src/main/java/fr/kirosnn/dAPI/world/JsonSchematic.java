package fr.kirosnn.dAPI.world;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Json schematic.
 */
public class JsonSchematic implements FileType {

    private static final int START = '#';
    private int currentChar = START - 1;

    /**
     * The Gson.
     */
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

    /**
     * Instantiates a new Json schematic.
     */
    public JsonSchematic() {
    }

    /**
     * Instantiates a new Json schematic.
     *
     * @param dataVersion      the data version
     * @param minecraftVersion the minecraft version
     * @param dimensions       the dimensions
     * @param palette          the palette
     * @param blocks           the blocks
     */
    public JsonSchematic(int dataVersion, String minecraftVersion, List<Integer> dimensions, List<String> palette, String blocks) {
        this.dataVersion = dataVersion;
        this.minecraftVersion = minecraftVersion;
        this.dimensions = dimensions;
        this.palette = palette;
        this.blocks = blocks;
        this.waypoints = new HashMap<>();
    }

    /**
     * Instantiates a new Json schematic.
     *
     * @param dataVersion      the data version
     * @param minecraftVersion the minecraft version
     * @param dimensions       the dimensions
     * @param palette          the palette
     * @param blocks           the blocks
     * @param waypoints        the waypoints
     */
    public JsonSchematic(int dataVersion, String minecraftVersion, List<Integer> dimensions, List<String> palette, String blocks, Map<String, List<String>> waypoints) {
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
        List<Integer> dimensions = Arrays.asList(dimensionVector.getBlockX(), dimensionVector.getBlockY(), dimensionVector.getBlockZ());
        List<String> palette = schematic.getPalette().stream().map(it -> it.getAsString(true)).collect(Collectors.toList());
        String serializedBlocks = String.join("", schematic.getBlocks().stream().map(this::getChar).collect(Collectors.toList()));

        Map<String, List<String>> waypoints = new HashMap<>();
        for (Map.Entry<String, List<Location>> entry : schematic.getWaypoints().entrySet()) {
            List<String> waypointList = new ArrayList<>();
            for (Location it : entry.getValue()) {
                waypointList.add(it.getX() + "," + it.getY() + "," + it.getZ() + "," + it.getYaw() + "," + it.getPitch());
            }
            waypoints.put(entry.getKey(), waypointList);
        }

        JsonSchematic jsonSchematic = new JsonSchematic(schematic.getDataVersion(), schematic.getMinecraftVersion(),
                dimensions, palette, serializedBlocks, waypoints);

        try {
            write(file, jsonSchematic);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Write.
     *
     * @param file the file
     * @param type the type
     * @throws IOException the io exception
     */
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

            List<org.bukkit.block.data.BlockData> palette = unparsedPalette.stream()
                    .map(Bukkit::createBlockData)
                    .collect(Collectors.toList());

            Vector dimensions = new Vector(serialized.dimensions.get(0), serialized.dimensions.get(1), serialized.dimensions.get(2));
            List<Short> blocks = serialized.blocks.chars()
                    .mapToObj(c -> fromChar((char) c))
                    .collect(Collectors.toList());

            Map<String, List<Location>> waypoints = new HashMap<>();
            if (dataVersion >= 2) {
                for (Map.Entry<String, List<String>> entry : serialized.waypoints.entrySet()) {
                    List<Location> locationList = new ArrayList<>();
                    for (String it : entry.getValue()) {
                        String[] parts = it.split(",");
                        locationList.add(new Location(null, Double.parseDouble(parts[0]),
                                Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                                Float.parseFloat(parts[3]), Float.parseFloat(parts[4])));
                    }
                    waypoints.put(entry.getKey(), locationList);
                }
            }

            return new Schematic(Schematic.DATA_VERSION, mcVersion, dimensions, palette, blocks, waypoints);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Read json schematic.
     *
     * @param file the file
     * @return the json schematic
     * @throws IOException the io exception
     */
    JsonSchematic read(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return GSON.fromJson(reader, JsonSchematic.class);
        }
    }

    /**
     * Gets char.
     *
     * @param id the id
     * @return the char
     */
// Avoids control chars
    String getChar(short id) {
        return chars.computeIfAbsent(id, it -> {
            do {
                currentChar++;
            } while (Character.isISOControl(currentChar));

            return Character.toString((char) currentChar);
        });
    }

    /**
     * From char short.
     *
     * @param c the c
     * @return the short
     */
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
