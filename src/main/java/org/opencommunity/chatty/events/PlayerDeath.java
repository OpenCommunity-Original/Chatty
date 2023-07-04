package org.opencommunity.chatty.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.opencommunity.chatty.utils.ConfigurationManager;
import org.opencommunity.chatty.utils.FormatUtil;

public class PlayerDeath implements Listener {

    private final String deathPrefix;

    public PlayerDeath(ConfigurationManager configManager) {
        this.deathPrefix = FormatUtil.replaceFormat(configManager.getString("death-prefix"));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Component deathMessageComponent = event.deathMessage();
        if (deathMessageComponent == null) {
            return;
        }
        String deathMessage = PlainTextComponentSerializer.plainText().serialize(deathMessageComponent);
        event.setDeathMessage(deathPrefix + deathMessage);
    }
}
