package org.opencommunity.chatty.functions;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.opencommunity.chatty.utils.*;

public class ChatCorrection implements Listener {
    private ConfigurationManager configManager;
    private final boolean antiCapsEnabled;
    private final boolean chatCorrectionEnabled;
    private final int antiCapsAmount;
    private final int minMessage;

    public ChatCorrection(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.antiCapsEnabled = configManager.getBoolean("anti-caps");
        this.chatCorrectionEnabled = configManager.getBoolean("chat-correction");
        this.antiCapsAmount = configManager.getInt("anti-caps-amount");
        this.minMessage = configManager.getInt("anti-caps-min");
    }

    public Component handleChat(String message, Player player) {
        if (antiCapsEnabled) {
            int capsPercentage = calculateCapsPercentage(message);
            if (capsPercentage > antiCapsAmount && (message.length() > minMessage)) {
                player.sendMessage(FormatUtil.replaceFormat(
                        LocaleAPI.getMessage(player,"anti-caps-message")));
                if (!chatCorrectionEnabled) {
                    return Component.text(message.toLowerCase());
                }
                return Component.text(message.substring(0, 1).toUpperCase() + message.substring(1).toLowerCase());
            }
        }
        if (chatCorrectionEnabled) {
            return Component.text(message.substring(0, 1).toUpperCase() + message.substring(1));
        }
        return Component.text(message);
    }

    private int calculateCapsPercentage(String message) {
        int caps = (int) message.codePoints().filter(Character::isUpperCase).count();
        return (int) Math.round((double) caps / message.length() * 100);
    }
}