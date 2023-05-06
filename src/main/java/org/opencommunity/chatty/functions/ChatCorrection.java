package org.opencommunity.chatty.functions;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.opencommunity.chatty.utils.*;

public class ChatCorrection implements Listener {

    private final int antiCapsAmount;
    private final int minMessage;
    private final Configuration config;

    public ChatCorrection(Configuration config) {
        this.config = config;
        this.antiCapsAmount = (int) config.getDouble("anti-caps-amount");
        this.minMessage = config.getInt("anti-caps-min");
    }

    public Component handleChat(String message, Player player) {
        if (config.getBoolean("anti-caps")) {
            int capsPercentage = calculateCapsPercentage(message);
            if (capsPercentage > antiCapsAmount && (message.length() > minMessage)) {
                player.sendMessage(FormatUtil.replaceFormat(
                        LocaleAPI.getMessage(player,"anti-caps-message")));
                if (!config.getBoolean("chat-correction")) {
                    return Component.text(message.toLowerCase());
                }
                return Component.text(message.substring(0, 1).toUpperCase() + message.substring(1).toLowerCase());
            }
        }
        if (config.getBoolean("chat-correction")) {
            return Component.text(message.substring(0, 1).toUpperCase() + message.substring(1));
        }
        return Component.text(message);
    }

    private int calculateCapsPercentage(String message) {
        int caps = (int) message.codePoints().filter(Character::isUpperCase).count();
        return (int) Math.round((double) caps / message.length() * 100);
    }
}