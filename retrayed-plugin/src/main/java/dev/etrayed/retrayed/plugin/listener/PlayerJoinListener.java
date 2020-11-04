package dev.etrayed.retrayed.plugin.listener;

import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.event.entity.SpawnPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Etrayed
 */
public class PlayerJoinListener extends RecordingListener {

    public PlayerJoinListener(RetrayedPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handleJoin(PlayerJoinEvent joinEvent) {
        Player player = joinEvent.getPlayer();

        addEvent(new SpawnPlayerEvent(player), player);
    }
}
