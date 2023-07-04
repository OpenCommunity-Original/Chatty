package org.opencommunity.chatty.functions;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.opencommunity.chatty.utils.ConfigurationManager;

public class ChatFormatter {
    private final String formatModer;
    private final String formatModerHover;
    private final String formatDefault;

    public ChatFormatter(ConfigurationManager configManager) {
        this.formatModer = configManager.getString("chat-formats.moder");
        this.formatModerHover = configManager.getString("chat-formats.moder-hover");
        this.formatDefault = configManager.getString("chat-formats.default");
    }

    public TextComponent formatMessage(Player player) {

        TextComponent format;
        if (player.hasPermission("opencommunity.moder")) {
            format = LegacyComponentSerializer.legacyAmpersand().deserialize(formatModer.replace("%player%", player.getDisplayName()))
                    .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(formatModerHover)));
        } else {
            format = LegacyComponentSerializer.legacyAmpersand().deserialize(formatDefault.replace("%player%", player.getDisplayName()));
        }

        return format;
    }
}