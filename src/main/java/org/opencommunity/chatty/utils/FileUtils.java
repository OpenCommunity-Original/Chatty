package org.opencommunity.chatty.utils;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    private static final List<Pattern> badWordPatterns = new ArrayList<>();

    public static void copyBadListFolder(Plugin plugin) {
        File sourceFolder = new File(plugin.getDataFolder(), "BadList");
        if (!sourceFolder.exists()) {
            sourceFolder.mkdirs();
        }

        try {
            File pluginFile = new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            ZipFile zipFile = new ZipFile(pluginFile);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith("BadList/") && !entry.isDirectory()) {
                    File targetFile = new File(sourceFolder, entryName.substring("BadList/".length()));
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    InputStream inputStream = zipFile.getInputStream(entry);
                    Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    inputStream.close();
                }
            }

            zipFile.close();
        } catch (Exception e) {
            plugin.getLogger().warning("Error copying BadList folder from plugin resources: " + e.getMessage());
        }
    }

    public static void loadBadWordPatterns(Plugin plugin) {
        badWordPatterns.clear(); // Clear the existing patterns before loading new ones

        File badListFolder = new File(plugin.getDataFolder(), "BadList");
        if (!badListFolder.exists()) {
            copyBadListFolder(plugin);
        }
        File[] txtFiles = badListFolder.listFiles();
        if (txtFiles != null) {
            for (File file : txtFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Pattern pattern = Pattern.compile(line, Pattern.CASE_INSENSITIVE);
                            badWordPatterns.add(pattern);
                        }
                    } catch (IOException e) {
                        plugin.getLogger().warning("Error loading BadList file: " + file.getName());
                    }
                }
            }
        }
    }

    public static List<Pattern> getBadWordPatterns() {
        return badWordPatterns;
    }
}