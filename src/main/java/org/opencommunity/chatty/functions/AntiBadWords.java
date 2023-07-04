package org.opencommunity.chatty.functions;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.FileUtils;
import org.opencommunity.chatty.utils.FormatUtil;
import org.opencommunity.chatty.utils.LocaleAPI;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiBadWords {
    private final boolean antiBadEnabled;
    private final int badWordsLimit;
    private final int badWordsReset;
    private final String badWordsBan;
    private final List<Pattern> badWordPatterns = FileUtils.getBadWordPatterns();
    private final Map<Player, Integer> warningCountMap = new HashMap<>();
    private final Map<Player, Instant> lastWarningTimeMap = new HashMap<>();
    Plugin plugin;

    public AntiBadWords(ConfigurationManager configManager, Plugin plugin) {
        this.plugin = plugin;
        this.antiBadEnabled = configManager.getBoolean("anti-bad-words");
        this.badWordsLimit = configManager.getInt("anti-bad-words-limit");
        this.badWordsReset = configManager.getInt("anti-bad-words-reset");
        this.badWordsBan = configManager.getString("anti-bad-words-ban");
    }

    public String handleChat(String message, AsyncChatEvent event) {
        if (!antiBadEnabled) return message;

        final Player player = event.getPlayer();

        Instant lastWarningTime = lastWarningTimeMap.get(player);
        if (lastWarningTime != null) {
            Instant currentTime = Instant.now();
            Duration timeSinceLastWarning = Duration.between(lastWarningTime, currentTime);
            if (timeSinceLastWarning.compareTo(Duration.ofMinutes(badWordsReset)) > 0) {
                // Last warning was from a time before the reset duration, reset the counter
                warningCountMap.remove(player);
                lastWarningTimeMap.remove(player);
            }
        }

        int warningCount = warningCountMap.getOrDefault(player, 0); // Retrieve the current warning count

        boolean hasWarnedPlayer = false; // Track if the player has been warned for any bad word

        for (Pattern pattern : badWordPatterns) {
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                // Match found, censor the bad word
                message = matcher.replaceAll("***");

                if (!hasWarnedPlayer) {
                    // Send alert or take further action
                    // For example, you can send a warning to the player or log the incident
                    sendAlert(player);
                    hasWarnedPlayer = true;

                    // Increment the warning count for the current chat message
                    warningCount++;

                    // Perform additional actions based on the bad word limit
                    if (warningCount > badWordsLimit) {
                        // Perform the ban action
                        banPlayer(player);
                        break; // Exit the loop as the player has been banned
                    }

                    // Record the current time as the last warning time for the player
                    lastWarningTimeMap.put(player, Instant.now());
                }
            }
        }

        // Update the warning count in the map
        warningCountMap.put(player, warningCount);

        return message;
    }

    private void sendAlert(Player player) {
        player.sendMessage(FormatUtil.replaceFormat(
                LocaleAPI.getMessage(player, "stop-message")));
    }

    private void banPlayer(Player player) {
        // Logic to ban the player
        String banCommand = badWordsBan.replace("%player%", player.getName());
        // Schedule the ban command to run on the main thread

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.runTask(plugin, () -> player.performCommand(banCommand));
    }
}