package org.opencommunity.chatty.commands;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.opencommunity.chatty.functions.LocalChat;
import org.opencommunity.chatty.utils.FormatUtil;

import java.util.Objects;

public class ChatCommands implements CommandExecutor {
    private final LocalChat localChat;

    private final String leftLocal;
    private final String chatCommand;
    private final String invalidLocal;
    private final String alreadyIn;
    private final String joinedLocal;
    private final Configuration config;

    public ChatCommands(Configuration config, LocalChat localChat) {
        this.config = config;
        this.localChat = localChat;
        this.leftLocal = FormatUtil.replaceFormat(config.getString("success-messages.left-local-chat"));
        this.chatCommand = FormatUtil.replaceFormat(config.getString("usage-messages.chat-command"));
        this.invalidLocal = FormatUtil.replaceFormat(config.getString("error-messages.invalid-local-chat-name"));
        this.alreadyIn = FormatUtil.replaceFormat(config.getString("error-messages.already-in-chat"));
        this.joinedLocal = FormatUtil.replaceFormat(config.getString("success-messages.joined-local-chat"));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Objects.requireNonNull(config.getString("error-messages.not-a-player")));
            return true;
        }

        if ((args.length == 0 ? "" : args[0].toLowerCase()).equals("leave")) {// Remove player from local chat
            localChat.removePlayer(player);
            player.sendMessage(" \n" + leftLocal + "\n ");
            localChat.sendChatLeaveMessage(player);
        } else { // Check if chat name is provided
            if (args.length < 1) {
                player.sendMessage(chatCommand);
                return true;
            }
            // Check if chat name is valid
            String chatName = args[0];
            if (!chatName.matches("^[a-zA-Z0-9]{4,14}$")) {
                player.sendMessage(invalidLocal);
                return true;
            }
            // Check if player is already in a chat
            if (localChat.isInChat(player)) {
                player.sendMessage(alreadyIn);
                return true;
            }
            // Add player to local chat
            localChat.addPlayer(player, chatName);
            player.sendMessage(" \n" + joinedLocal + "\n ");
            localChat.sendChatJoinMessage(player);
        }
        return true;
    }
}
