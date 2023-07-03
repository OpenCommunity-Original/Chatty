package org.opencommunity.chatty.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationManager {
    private final Plugin plugin;
    private final Map<String, Object> configValues;

    public ConfigurationManager(Plugin plugin) {
        this.plugin = plugin;
        this.configValues = new HashMap<>();
    }

    public static ConfigurationManager getInstance(Plugin plugin) {
        return new ConfigurationManager(plugin);
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        for (String path : config.getKeys(true)) {
            configValues.put(path, config.get(path));
        }
    }

    public int getInt(String path) {
        return (int) configValues.getOrDefault(path, 0);
    }

    public boolean getBoolean(String path) {
        return (boolean) configValues.getOrDefault(path, false);
    }

    public String getString(String path) {
        return (String) configValues.getOrDefault(path, "");
    }

    public List<String> getStringList(String path) {
        Object value = configValues.getOrDefault(path, Collections.emptyList());
        if (value instanceof List) {
            return (List<String>) value;
        }
        return Collections.emptyList();
    }
}