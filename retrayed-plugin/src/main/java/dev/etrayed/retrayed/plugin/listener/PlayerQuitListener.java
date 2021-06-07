package dev.etrayed.retrayed.plugin.listener;

import dev.etrayed.retrayed.plugin.RetrayedPlugin;
import dev.etrayed.retrayed.plugin.event.entity.RemoveEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Etrayed
 */
public class PlayerQuitListener extends RecordingListener {

    public PlayerQuitListener(RetrayedPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent quitEvent) {
        Player player = quitEvent.getPlayer();

        addEvent(new RemoveEntityEvent(player.getEntityId()), player);
    }

    @EventHandler
    public void handleKick(PlayerKickEvent kickEvent) {
        Player player = kickEvent.getPlayer();

        addEvent(new RemoveEntityEvent(player.getEntityId()), player);
    }
}
