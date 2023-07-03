package org.opencommunity.chatty;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
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

    public AsyncChat asyncChat;
    public ChatCommands chatCommands;
    private ConfigurationManager configManager;

    @Override
    public void onEnable() {
        // Save the default config file if it doesn't already exist and reload the latest version
        saveAndReloadConfig();
        // Initialize the chat command and register it with Bukkit
        initializeChatCommand();
        // Initialize and register the event listeners
        initializeEventListeners();
        // LocaleAPI
        LocaleAPI localeAPI = new LocaleAPI();
        Bukkit.getPluginManager().registerEvents(localeAPI, this);
        localeAPI.loadSupportedLocales(this);
    }

    private void saveAndReloadConfig() {
        configManager = ConfigurationManager.getInstance(this);
        configManager.loadConfig();

        // Load bad words files
        loadBadWordPatterns(this);
    }

    private void initializeChatCommand() {
        ChatFormatter chatFormatter = new ChatFormatter(configManager);
        LocalChat localChat = new LocalChat(configManager);
        ChatCorrection chatCorrection = new ChatCorrection(configManager);
        AntiBadWords antiBadWords = new AntiBadWords(configManager, this);
        AntiFlood antiFlood = new AntiFlood(configManager);
        asyncChat = new AsyncChat(chatCorrection, chatFormatter, localChat, antiBadWords, antiFlood);
        chatCommands = new ChatCommands(configManager, localChat);
        Objects.requireNonNull(getCommand("chat")).setExecutor(chatCommands);
    }

    private void initializeEventListeners() {
        PlayerDeath playerDeath = new PlayerDeath(configManager);
        Announcer announcer = new Announcer(this, configManager);
        PlayerJoinLeave playerJoinLeave = new PlayerJoinLeave(configManager);

        Bukkit.getPluginManager().registerEvents(playerDeath, this);
        Bukkit.getPluginManager().registerEvents(asyncChat, this);
        Bukkit.getPluginManager().registerEvents(playerJoinLeave, this);

        announcer.startAnnouncementTask();
    }

    public void onDisable() {
        // Unregister events and commands
        HandlerList.unregisterAll();
    }
}