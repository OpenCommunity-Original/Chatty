package org.opencommunity.chatty.functions;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.FormatUtil;
import org.opencommunity.chatty.utils.LocaleAPI;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AntiFlood {
    private final boolean antiFloodEnabled;
    private final int antiFloodAmount;
    private final int antiFloodSimilarity;
    private final int checkTheLast;
    private final Map<Player, LinkedList<String>> playerRecentMessages = new HashMap<>();

    public AntiFlood(ConfigurationManager configManager) {
        this.antiFloodEnabled = configManager.getBoolean("anti-flood");
        this.antiFloodAmount = configManager.getInt("anti-flood-amount");
        this.antiFloodSimilarity = configManager.getInt("anti-flood-similarity");
        this.checkTheLast = configManager.getInt("check-the-latest");
    }

    public void handleChat(String message, AsyncChatEvent event) {
        if (!antiFloodEnabled) return;

        final Player player = event.getPlayer();

        LinkedList<String> playerMessages = playerRecentMessages.computeIfAbsent(player, k -> new LinkedList<>());

        if (isFloodDetected(playerMessages, message)) {
            // Flood detected, cancel the event
            event.setCancelled(true);
            sendAlert(player);
        } else {
            // Add the message to the player's recent messages list
            addRecentMessage(playerMessages, message);
        }
    }

    private boolean isFloodDetected(LinkedList<String> playerMessages, String message) {
        // Check if the message matches the flood criteria
        int similarityCount = 0;

        for (String recentMessage : playerMessages) {
            double similarity = calculateSimilarity(message, recentMessage);
            if (similarity >= antiFloodSimilarity) {
                similarityCount++;
            }
        }

        // Check if the flood criteria are met
        return similarityCount >= antiFloodAmount;
    }

    private void addRecentMessage(LinkedList<String> playerMessages, String message) {
        // Add the message to the player's recent messages list
        playerMessages.add(message);

        // Keep the recent messages list within the specified limit
        while (playerMessages.size() > checkTheLast) {
            playerMessages.removeFirst();
        }
    }

    private double calculateSimilarity(String message1, String message2) {
        // Calculate the similarity between two messages
        int maxLength = Math.max(message1.length(), message2.length());
        int commonLength = 0;

        for (int i = 0; i < maxLength; i++) {
            if (i < message1.length() && i < message2.length() && message1.charAt(i) == message2.charAt(i)) {
                commonLength++;
            }
        }

        return (double) commonLength / maxLength * 100;
    }

    private void sendAlert(Player player) {
        player.sendMessage(FormatUtil.replaceFormat(
                LocaleAPI.getMessage(player, "flood-message")));
    }
}
