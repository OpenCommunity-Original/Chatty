package org.opencommunity.chatty.functions;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.FormatUtil;
import org.opencommunity.chatty.utils.LocaleAPI;

import java.util.HashMap;
import java.util.Map;

public class LocalChat {
    private ConfigurationManager configManager;

    public Map<String, String> chattyPlayers = new HashMap<>();
    public Map<Player, String> previousMessages = new HashMap<>();
    private final String localChatPrefix;

    public LocalChat(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.localChatPrefix = configManager.getString("local-chat-prefix");
    }

    public boolean isInChat(Player player) {
        return chattyPlayers.containsKey(player.getName());
    }


    public void addPlayer(Player player, String chatName) {
        // Add player to local chat
        chattyPlayers.put(player.getName(), chatName);
    }

    public void removePlayer(Player player) {
        chattyPlayers.remove(player.getName());
    }

    public void sendChatJoinMessage(Player player) {
        // Send message to players in the same local chat
        for (Map.Entry<String, String> entry : chattyPlayers.entrySet()) {
            if (entry.getValue().equals(chattyPlayers.get(player.getName()))) {
                Player chattyPlayer = Bukkit.getPlayer(entry.getKey());
                if (chattyPlayer != null) {
                    chattyPlayer.sendMessage(FormatUtil
                            .replaceFormat(LocaleAPI.getMessage(player, "local-chat-join")
                            .replace("%player%", player.getName())));
                }
            }
        }
    }

    public void sendChatLeaveMessage(Player player) {
        // Send message to players in the same local chat
        for (Map.Entry<String, String> entry : chattyPlayers.entrySet()) {
            Player chattyPlayer = Bukkit.getPlayer(entry.getKey());
            if (chattyPlayer != null) {
                chattyPlayer.sendMessage(FormatUtil
                        .replaceFormat(LocaleAPI.getMessage(player, "local-chat-leave")
                        .replace("%player%", player.getName())));
            }
        }
    }

    public void handleLocalChat(AsyncChatEvent event, Player player, String message) {

        // If the player is not in a local chat, return immediately
        if (!chattyPlayers.containsKey(player.getName())) {
            return;
        }

        // If the message starts with "!", send it to the global chat
        if (message.startsWith("!")) {
            handleGlobalChat(event, message.substring(1));
            return;
        }

        // If the message is a duplicate, cancel the event and return
        if (isDuplicateMessage(player, message)) {
            event.setCancelled(true);
            return;
        }

        // Log the message to the server console
        logLocalChatMessage(player, message);

        // Send the message to all players in the same local chat
        sendLocalChatMessage(player, message);

        // Cancel the event so the message is not sent to the global chat
        event.setCancelled(true);
    }

    private void handleGlobalChat(AsyncChatEvent event, String message) {
        // Remove the "!" and send the message to the global chat
        Component correctedMessage = Component.text(message);
        event.message(correctedMessage);
    }

    private boolean isDuplicateMessage(Player player, String message) {
        // Check if the message is the same as the player's previous message
        String previousMessage = previousMessages.getOrDefault(player, "");
        return previousMessage.equals(message);
    }

    private void logLocalChatMessage(Player player, String message) {
        // Log the message to the server console in the format "[chatName] playerName: message"
        String chatName = chattyPlayers.get(player.getName());
        Bukkit.getLogger().info("[" + chatName + "] " + player.getName() + ": " + message);
    }

    private void sendLocalChatMessage(Player player, String message) {
        // Format the message with the chat prefix and send it to all players in the same local chat
        String chatName = chattyPlayers.get(player.getName());
        String chatPrefix = localChatPrefix.replace("%chatname%", chatName).replace("%player%", player.getName());
        String formattedMessage = chatPrefix + message.substring(0, 1).toUpperCase() + message.substring(1);
        String capitalizedMessage = FormatUtil.replaceFormat(formattedMessage);

        for (Map.Entry<String, String> entry : chattyPlayers.entrySet()) {
            if (!entry.getValue().equals(chatName)) {
                continue;
            }

            Player chattyPlayer = Bukkit.getPlayer(entry.getKey());
            if (chattyPlayer == null) {
                continue;
            }

            chattyPlayer.sendMessage(capitalizedMessage);
        }
    }
}
