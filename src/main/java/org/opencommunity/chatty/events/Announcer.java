package org.opencommunity.chatty.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.Listener;
import org.opencommunity.chatty.Main;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.FormatUtil;

import java.util.List;
import java.util.Random;

public class Announcer implements Listener {
    private final Main plugin;
    private final String announcementPrefix;
    private final int announcementInterval;
    private final List<String> announcementMessages;

    public Announcer(Main plugin, ConfigurationManager configManager) {
        this.plugin = plugin;
        this.announcementInterval = configManager.getInt("announcement-interval");
        this.announcementPrefix = configManager.getString("announcement-prefix");
        this.announcementMessages = configManager.getStringList("announcement-messages");
    }

    public void startAnnouncementTask() {
        if (announcementMessages.size() <= 0) {
            return;
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            String announcement = announcementPrefix + announcementMessages.get(new Random().nextInt(announcementMessages.size()));
            Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(FormatUtil.replaceFormat(announcement)));
        }, (long) announcementInterval * 20 * 60, (long) announcementInterval * 20 * 60);
    }
}
