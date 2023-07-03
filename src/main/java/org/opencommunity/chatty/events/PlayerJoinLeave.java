package org.opencommunity.chatty.events;

import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.FormatUtil;

import java.util.List;
import java.util.Random;

public class PlayerJoinLeave implements Listener {

    private final String joinPrefix;
    private final List<String> joinMessages;
    private final String leavePrefix;
    private final List<String> leaveMessages;

    public PlayerJoinLeave(ConfigurationManager configManager) {
        this.joinPrefix = configManager.getString("join-prefix");
        this.joinMessages = configManager.getStringList("join-messages");
        this.leavePrefix = configManager.getString("leave-prefix");
        this.leaveMessages = configManager.getStringList("leave-messages");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (joinMessages.size() <= 0) {
            return;
        }
        Player player = event.getPlayer();
        String joinMessage = FormatUtil.replaceFormat(joinPrefix + joinMessages.get(new Random().nextInt(joinMessages.size())));
        joinMessage = joinMessage.replace("%player%", player.getName());
        event.setJoinMessage(joinMessage);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        if (leaveMessages.size() <= 0) {
            return;
        }

        Player player = event.getPlayer();
        String leaveMessage = FormatUtil.replaceFormat(leavePrefix + leaveMessages.get(new Random().nextInt(leaveMessages.size())));
        leaveMessage = leaveMessage.replace("%player%", player.getName());
        event.setQuitMessage(leaveMessage);
    }
}