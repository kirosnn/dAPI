package fr.kirosnn.dAPI.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * The interface File type.
 */
public interface FileType {

    /**
     * Save boolean.
     *
     * @param schematic the schematic
     * @param file      the file
     * @return the boolean
     */
    boolean save(@NotNull Schematic schematic, @NotNull File file);

    /**
     * Load schematic.
     *
     * @param file the file
     * @return the schematic
     */
    @Nullable Schematic load(@NotNull File file);

}