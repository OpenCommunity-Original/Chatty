package org.opencommunity.chatty;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.opencommunity.chatty.commands.ChatCommands;
import org.opencommunity.chatty.events.Announcer;
import org.opencommunity.chatty.events.AsyncChat;
import org.opencommunity.chatty.events.PlayerDeath;
import org.opencommunity.chatty.events.PlayerJoinLeave;
import org.opencommunity.chatty.functions.*;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.LocaleAPI;

import java.util.Objects;

import static org.opencommunity.chatty.utils.FileUtils.loadBadWordPatterns;

public class Main extends JavaPlugin {

    // Plugin components
    private AsyncChat asyncChat;
    private ConfigurationManager configManager;

    @Override
    public void onEnable() {
        // Save the default config file if it doesn't already exist and reload the latest version
        saveAndReloadConfig();

        // Initialize the chat command and register it with Bukkit
        initializeChatCommand();

        // Initialize and register the event listeners
        initializeEventListeners();

        // Initialize and register LocaleAPI event listener
        initializeLocaleAPI();
    }

    private void saveAndReloadConfig() {
        // Create or load the configuration manager for this plugin
        configManager = ConfigurationManager.getInstance(this);
        configManager.loadConfig();

        // Load bad words files
        loadBadWordPatterns(this);
    }

    private void initializeChatCommand() {
        // Create and set up chat components
        ChatFormatter chatFormatter = new ChatFormatter(configManager);
        LocalChat localChat = new LocalChat(configManager);
        ChatCorrection chatCorrection = new ChatCorrection(configManager);
        AntiBadWords antiBadWords = new AntiBadWords(configManager, this);
        AntiFlood antiFlood = new AntiFlood(configManager);
        asyncChat = new AsyncChat(chatCorrection, chatFormatter, localChat, antiBadWords, antiFlood);
        ChatCommands chatCommands = new ChatCommands(configManager, localChat);

        // Register the chat command with Bukkit
        Objects.requireNonNull(getCommand("chat")).setExecutor(chatCommands);
    }

    private void initializeEventListeners() {
        // Create and register event listeners
        PlayerDeath playerDeath = new PlayerDeath(configManager);
        Announcer announcer = new Announcer(this, configManager);
        PlayerJoinLeave playerJoinLeave = new PlayerJoinLeave(configManager);

        Bukkit.getPluginManager().registerEvents(playerDeath, this);
        Bukkit.getPluginManager().registerEvents(asyncChat, this);
        Bukkit.getPluginManager().registerEvents(playerJoinLeave, this);

        // Start the announcer task
        announcer.startAnnouncementTask();
    }

    private void initializeLocaleAPI() {
        // Create and register LocaleAPI event listener
        LocaleAPI localeAPI = new LocaleAPI();
        Bukkit.getPluginManager().registerEvents(localeAPI, this);
        localeAPI.loadSupportedLocales(this);
    }

    @Override
    public void onDisable() {
        // Unregister events and commands when the plugin is disabled
        HandlerList.unregisterAll();
    }
}
