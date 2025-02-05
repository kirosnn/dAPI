package fr.kirosnn.dAPI.schematic;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * The type Zip schematic.
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
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().equals("schematic.json")) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream))) {
                        return GSON.fromJson(reader, JsonSchematic.class);
                    }
                }
            }
            throw new IOException("No valid schematic.json found in zip file");
        }
    }
}