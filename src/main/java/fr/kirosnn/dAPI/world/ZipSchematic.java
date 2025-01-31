package fr.kirosnn.dAPI.world;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipSchematic extends JsonSchematic {

    @Override
    void write(File file, JsonSchematic type) throws IOException {
        ZipOutputStream zipOutputStream = null;
        BufferedWriter writer = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
            writer = new BufferedWriter(new OutputStreamWriter(zipOutputStream));

            zipOutputStream.putNextEntry(new ZipEntry("schematic.json"));

            GSON.toJson(type, writer);
            writer.flush();

            zipOutputStream.closeEntry();
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (zipOutputStream != null) {
                zipOutputStream.close();
            }
        }
    }

    @Override
    JsonSchematic read(File file) throws IOException {
        ZipInputStream zipInputStream = null;
        BufferedReader reader = null;
        try {
            zipInputStream = new ZipInputStream(new FileInputStream(file));
            reader = new BufferedReader(new InputStreamReader(zipInputStream));

            ZipEntry entry = zipInputStream.getNextEntry();
            if (entry == null) {
                throw new IOException("No entries in zip file");
            }

            return GSON.fromJson(reader, JsonSchematic.class);
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (zipInputStream != null) {
                zipInputStream.close();
            }
        }
    }
}