package fr.kirosnn.dAPI.world;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Extension of {@link JsonSchematic} that represents a schematic stored in a zip file.
 * This has increased storage efficiency compared to {@link JsonSchematic}, at the cost
 * of readability.
 */
public class ZipSchematic extends JsonSchematic {

    @Override
    void write(File file, JsonSchematic type) throws IOException {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zipOutputStream))) {
            zipOutputStream.putNextEntry(new ZipEntry("schematic.json"));

            GSON.toJson(type, writer);
            writer.flush();

            zipOutputStream.closeEntry();
        }
    }

    @Override
    JsonSchematic read(File file) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
             BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            if (entry == null) {
                throw new IOException("No entries in zip file");
            }

            return GSON.fromJson(reader, JsonSchematic.class);
        }
    }
}