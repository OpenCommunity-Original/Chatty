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
import org.opencommunity.chatty.functions.ChatCorrection;
import org.opencommunity.chatty.functions.ChatFormatter;
import org.opencommunity.chatty.functions.LocalChat;
import org.opencommunity.chatty.utils.LocaleAPI;

import java.util.Objects;

public class Main extends JavaPlugin {

    public AsyncChat asyncChat;
    public ChatCommands chatCommands;
    private Configuration config;

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
        saveDefaultConfig();
        reloadConfig();
        config = getConfig();
    }

    private void initializeChatCommand() {
        ChatFormatter chatFormatter = new ChatFormatter(config);
        LocalChat localChat = new LocalChat(config);
        asyncChat = new AsyncChat(new ChatCorrection(config), chatFormatter, localChat);
        chatCommands = new ChatCommands(config, localChat);
        Objects.requireNonNull(getCommand("chat")).setExecutor(chatCommands);
    }

    private void initializeEventListeners() {
        PlayerDeath playerDeath = new PlayerDeath(config);
        Announcer announcer = new Announcer(this, config);
        PlayerJoinLeave playerJoinLeave = new PlayerJoinLeave(config);

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