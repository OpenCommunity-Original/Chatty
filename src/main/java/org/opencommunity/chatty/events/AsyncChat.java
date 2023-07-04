package org.opencommunity.chatty.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.opencommunity.chatty.functions.*;

public class AsyncChat implements Listener {

    private final ChatCorrection chatCorrection;
    private final ChatFormatter chatFormatter;
    private final LocalChat localChat;
    private final AntiBadWords antiBadWords;
    private final AntiFlood antiFlood;

    public AsyncChat(ChatCorrection chatCorrection, ChatFormatter chatFormatter, LocalChat localChat, AntiBadWords antiBadWords, AntiFlood antiFlood) {
        this.chatCorrection = chatCorrection;
        this.chatFormatter = chatFormatter;
        this.localChat = localChat;
        this.antiBadWords = antiBadWords;
        this.antiFlood = antiFlood;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();

        // Handle local chat
        String message = LegacyComponentSerializer.legacyAmpersand().serialize(event.message());
        localChat.handleLocalChat(event, message);

        if (event.isCancelled()) {
            return;
        }

        // Format player's chat message
        final TextComponent textComponent = formatChatMessage(event);
        // Format console chat message
        final TextComponent consoleTextComponent = Component.text(player.getName() + ": " + message);

        event.renderer((source, sourceDisplayName, message1, viewer) -> {
            if (viewer instanceof ConsoleCommandSender) {
                return consoleTextComponent;
            }
            return textComponent;
        });
    }

    private TextComponent formatChatMessage(AsyncChatEvent event) {
        final Player player = event.getPlayer();
        // Build player chat format
        TextComponent format = chatFormatter.formatMessage(player);

        // Chat Correction
        String message = LegacyComponentSerializer.legacyAmpersand().serialize(event.message());

        // Anti-Flood
        antiFlood.handleChat(message, event);

        if (event.isCancelled()) {
            return Component.empty();
        }

        // Anti-Bad-Words
        message = antiBadWords.handleChat(message, event);

        // Chat correction
        Component correctedMessage;
        correctedMessage = chatCorrection.handleChat(message, player);

        // Final message
        return Component.empty().append(format).append(correctedMessage);
    }
}
