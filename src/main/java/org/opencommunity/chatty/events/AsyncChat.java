package org.opencommunity.chatty.events;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.opencommunity.chatty.functions.ChatCorrection;
import org.opencommunity.chatty.functions.ChatFormatter;
import org.opencommunity.chatty.functions.LocalChat;

public class AsyncChat implements Listener {

    private final ChatCorrection chatCorrection;
    private final ChatFormatter chatFormatter;
    private final LocalChat localChat;

    public AsyncChat(ChatCorrection chatCorrection, ChatFormatter chatFormatter, LocalChat localChat) {
        this.chatCorrection = chatCorrection;
        this.chatFormatter = chatFormatter;
        this.localChat = localChat;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncChatEvent event) {
        final Player player = event.getPlayer();

        // Handle local chat
        String message = LegacyComponentSerializer.legacyAmpersand().serialize(event.message());
        localChat.handleLocalChat(event, player, message);

        if (event.isCancelled()) {
            return;
        }

        // Format player's chat message
        final TextComponent textComponent = formatChatMessage(player, event);
        // Format console chat message
        final TextComponent consoleTextComponent = Component.text(player.getName() + ": " + message);

        event.renderer(new ChatRenderer() {
            @Override
            public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
                if (viewer instanceof ConsoleCommandSender) {
                    return consoleTextComponent;
                }
                return textComponent;
            }
        });
    }

    private TextComponent formatChatMessage(Player player, AsyncChatEvent event) {
        // Build player chat format
        TextComponent format = chatFormatter.formatMessage(player);

        // Chat Correction
        String message = LegacyComponentSerializer.legacyAmpersand().serialize(event.message());
        Component correctedMessage = chatCorrection.handleChat(message, player);

        // Final message
        return Component.empty().append(format).append(correctedMessage);
    }
}
