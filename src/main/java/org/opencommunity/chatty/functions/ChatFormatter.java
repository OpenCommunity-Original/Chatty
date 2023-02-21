package org.opencommunity.chatty.functions;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class ChatFormatter {
    private final String formatModer;
    private final String formatModerHover;
    private final String formatDefault;

    public ChatFormatter(Configuration config) {
        this.formatModer = config.getString("chat-formats.moder");
        this.formatModerHover = config.getString("chat-formats.moder-hover");
        this.formatDefault = config.getString("chat-formats.default");
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